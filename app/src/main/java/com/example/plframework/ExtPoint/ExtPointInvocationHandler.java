/**
 * 
 */
package com.example.plframework.ExtPoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.util.Log;

public class ExtPointInvocationHandler implements InvocationHandler {
	private static String TAG = "PointInvocationHandler";
	private Object realObj;
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		Log.d(TAG, proxy.getClass().getName());
		result = method.invoke(realObj, args);
		return result;
	}
	
	public ExtPointInvocationHandler(Object realSubject) {
		this.realObj = realSubject;
	}

}
