package com.xbw.unman.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreference {

	private static final String SHAREDPREFERENCES_NAME = "keep_login";
	private static final String KEEP_LOGIN_ACTIVITY = "login_activity";
	private static final String KEEP_LOGIN_ACTIVITY_ID = "login_activity_id";
	private static final String KEEP_LOGIN_ACTIVITY_NAME="login_activity_name";
	private static final String KEEP_LOGIN_ACTIVITY_LONGITUDE="login_activity_longitude";
	private static final String KEEP_LOGIN_ACTIVITY_LATITUDE="login_activity_latitude";
	private Context context;

	public SharedPreference(Context context) {
		this.context = context;
	}

	public void KeepLogin(String ID) {
		SharedPreferences settings = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEEP_LOGIN_ACTIVITY, "1");
		editor.putString(KEEP_LOGIN_ACTIVITY_ID, ID);
		Log.i("*****************", "true");
		editor.commit();
	}
	public void KeepName(String NAME){
		SharedPreferences settings = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEEP_LOGIN_ACTIVITY_NAME, NAME);
		editor.commit();
	}
	public void KeepLong(String Long){
		SharedPreferences settings = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEEP_LOGIN_ACTIVITY_LONGITUDE, Long);
		editor.commit();
	}
	public void KeepLat(String Lat){
		SharedPreferences settings = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEEP_LOGIN_ACTIVITY_LATITUDE, Lat);
		editor.commit();
	}
	public void DisconnectLogin() {
		SharedPreferences settings = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEEP_LOGIN_ACTIVITY, "0");
		editor.commit();
	}

	public boolean isLogin(String className) {
		if (context == null || className == null
				|| "".equalsIgnoreCase(className))
			return false;
		String mResultStr = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEEP_LOGIN_ACTIVITY, "");
		if (mResultStr.equalsIgnoreCase("1"))
			return true;
		else
			return false;
	}

	public String getID() {
		String mResultStr = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEEP_LOGIN_ACTIVITY_ID, "");
		return mResultStr;
	}
	public String getNAME() {
		String mResultStr = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEEP_LOGIN_ACTIVITY_NAME, "");
		return mResultStr;
	}
	public String getLong() {
		String mResultStr = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEEP_LOGIN_ACTIVITY_LONGITUDE, "");
		return mResultStr;
	}
	public String getLat() {
		String mResultStr = context.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(
				KEEP_LOGIN_ACTIVITY_LATITUDE, "");
		return mResultStr;
	}

}
