package com.philipslight.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    static final String PREF_USER_NAME= "userName";
    static final String PREF_USER_PASSWORD= "password";
    static final String PREF_NAME= "name";
    static final String PREF_PROFILE_IMAGE= "profileImage";
    static final String PREF_USER_TYPE= "userType";
    static final String PREF_USER_LANG= "userLanguage";
    static final String PREF_USER_KEEP_LOGIN= "userKeepLogin";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // used for login or create user to store login data in shared preference
    public static void setUser(Context ctx, User user)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, user.getUserName());
        editor.putString(PREF_USER_PASSWORD, user.getPassword());
        editor.putString(PREF_NAME, user.getName());
        editor.putString(PREF_PROFILE_IMAGE, user.getProfileImage());
        editor.putString(PREF_USER_TYPE, user.getUserType());
        editor.putString(PREF_USER_LANG, user.getLanguage());
        editor.putBoolean(PREF_USER_KEEP_LOGIN, user.isKeepLogin());
        editor.commit();
    }

    public static User getUser(Context ctx)
    {
        User user = new User();
        user.setUserName(getSharedPreferences(ctx).getString(PREF_USER_NAME, ""));
        user.setPassword(getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, ""));
        user.setName(getSharedPreferences(ctx).getString(PREF_NAME, ""));
        user.setProfileImage(getSharedPreferences(ctx).getString(PREF_PROFILE_IMAGE, ""));
        user.setUserType(getSharedPreferences(ctx).getString(PREF_USER_TYPE, ""));
        user.setLanguage(getSharedPreferences(ctx).getString(PREF_USER_LANG, "ar"));
        user.setKeepLogin(getSharedPreferences(ctx).getBoolean(PREF_USER_KEEP_LOGIN, true));
        return user;
    }

    // used for logout to clear shared data.
    public static void clearData(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

    // used for logout to clear shared data.
//    public static void clearData(Context ctx)
//    {
//        getSharedPreferences(ctx).edit().remove(PREF_USER_NAME).commit();
//        getSharedPreferences(ctx).edit().remove(PREF_USER_PASSWORD).commit();
//    }
}