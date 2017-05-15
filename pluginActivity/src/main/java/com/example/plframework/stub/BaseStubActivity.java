/**
 * 
 */
package com.example.plframework.stub;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.pluginactivity.R;

public class BaseStubActivity extends Activity {
	private static String TAG = "BaseStubActivity";
	private AssetManager asm;
	private Resources res;
	private Theme thm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, this.getPackageName());
		if (!getPackageName().equals(Config.packageName)) {
			String resPath = getDir("dex", Context.MODE_PRIVATE).getAbsolutePath() + "/" + Config.APKName;
			Log.e(TAG, "onCreate - resPath: " + resPath);
			try {
				AssetManager am = (AssetManager) AssetManager.class
						.newInstance();
				am.getClass().getMethod("addAssetPath", String.class)
						.invoke(am, resPath);
				asm = am;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	
			Resources superRes = super.getResources();
	
			res = new Resources(asm, superRes.getDisplayMetrics(),
					superRes.getConfiguration());
	
			thm = res.newTheme();
			thm.setTo(super.getTheme());

			Log.d(TAG, "onCreate - activity_main: " + this.getResources().getResourceName(R.layout.activity_main));
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public AssetManager getAssets() {
		return asm == null ? super.getAssets() : asm;
	}

	@Override
	public Resources getResources() {
		return res == null ? super.getResources() : res;
	}

	@Override
	public Theme getTheme() {
		return thm == null ? super.getTheme() : thm;
	}
}
