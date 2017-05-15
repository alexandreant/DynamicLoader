package com.example.plframework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.plframework.ExtPoint.MessageExtPointInterace;

import dalvik.system.DexClassLoader;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	public static ClassLoader ORIGINAL_LOADER;
	public static ClassLoader CUSTOM_LOADER = null;
	public static Bitmap mBitmap ;
	public static Map<String, ClassLoader> PLUGIN_LOADER = new HashMap<String, ClassLoader>();
	public static Map<String, MessageExtPointInterace> PLUGIN_PROXY = new HashMap<String, MessageExtPointInterace>();
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			Context mBase = new Smith<Context>(this, "mBase").get();

			Object mPackageInfo = new Smith<Object>(mBase, "mPackageInfo")
					.get();

			Smith<ClassLoader> sClassLoader = new Smith<ClassLoader>(
					mPackageInfo, "mClassLoader");
			ClassLoader mClassLoader = sClassLoader.get();
			ORIGINAL_LOADER = mClassLoader;

			MyClassLoader cl = new MyClassLoader(mClassLoader);
			sClassLoader.set(cl);

			loadAllPlugin();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//FIXME: lazy load
	public void loadAllPlugin() {
		try {
			AssetManager asset = getAssets();
			for (String title : asset.list("apks")) {
				String path = "apks/" + title;
				File dexInternalStoragePath = getDir("dex", Context.MODE_PRIVATE);
				dexInternalStoragePath.mkdirs();
				File f = new File(dexInternalStoragePath, title);
				InputStream fis = getAssets().open(path);
				FileOutputStream fos = new FileOutputStream(f);
				byte[] buffer = new byte[0xFF];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fis.close();
				fos.close();

				File optimizedDexPath = getDir("outdex", Context.MODE_PRIVATE);
				optimizedDexPath.mkdirs();
				DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(),
							optimizedDexPath.getAbsolutePath(), null,
							MyApplication.ORIGINAL_LOADER.getParent());
				PLUGIN_LOADER.put(title, dcl);

				// plugin res path
				String resPath = this.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath() + "/" + title;
				AssetManager am = (AssetManager) AssetManager.class.newInstance();
				// build plugin resource environment
				am.getClass().getMethod("addAssetPath", new Class[]{String.class}).invoke(am, resPath);
				Class<?> pluginClass = ((ClassLoader)dcl).loadClass("com.example.plframework.stub.stubActivity");
				final Object pluginIns = pluginClass.newInstance();

                for (Method method : pluginIns.getClass().getMethods()) {
//                    Log.d(TAG, method.getName());
                }

				Resources superRes = this.getResources();
				Resources res = new Resources(am, superRes.getDisplayMetrics(), superRes.getConfiguration());
				//replace plugin resource, now u can use pluginIns access the plugin resource
				new Smith<Object>(pluginIns, "res").set(res);

				Log.d(TAG, pluginIns.getClass().getName());
				Log.d(TAG, pluginIns.getClass().getMethod("getSummary").invoke(pluginIns).toString());
				mBitmap = (Bitmap)pluginIns.getClass().getMethod("getIconPath").invoke(pluginIns);

//				Object proxy =  Proxy.newProxyInstance(pluginIns.getClass().getClassLoader(),
//						pluginIns.getClass().getInterfaces(), new InvocationHandler() {
//
//							@Override
//							public Object invoke(Object proxy, Method method, Object[] args)
//									throws Throwable {
//								Log.d(TAG, method.getName());
//								return method.invoke(pluginIns, args);
//							}
//						});
//				Log.d(TAG, proxy.getClass().getName());
				Log.d(TAG, title + " loaded, try launch again");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	class MyClassLoader extends ClassLoader {
		public MyClassLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> loadClass(String className)
				throws ClassNotFoundException {
			if (CUSTOM_LOADER != null) {
				if (className.startsWith("com.")) {
					Log.i("classloader", "loadClass( " + className + " )");
				}
				try {
					Class<?> c = CUSTOM_LOADER.loadClass(className);

					if (c != null)
						return c;

                    Log.i("classloader", "classe do CUSTOM_LOADER é null ");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Class<?> c = getSystemClassLoader().loadClass(className);
					if (c != null)
						return c;
					else
						return super.loadClass(className);
				}
			}
			return super.loadClass(className);
		}
	}


}
