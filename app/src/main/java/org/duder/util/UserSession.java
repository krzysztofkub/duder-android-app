package org.duder.util;

import android.content.SharedPreferences;

import com.facebook.login.LoginManager;

import org.duder.model.LoggedAccount;

public class UserSession {
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String NICKNAME = "NICKNAME";
    public static final String TOKEN = "TOKEN";

    public static final String PREF_NAME = "org.duder.util.UserSession";

    public static void storeUserSession(LoggedAccount loggedAccount, SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NICKNAME, loggedAccount.getNickname());
        editor.putString(TOKEN, loggedAccount.getSessionToken());
        editor.putString(IMAGE_URL, loggedAccount.getImageUrl());
        editor.commit();
    }

    public static boolean isLoggedIn(SharedPreferences prefs) {
        String string = prefs.getString(TOKEN, "");
        return !string.isEmpty();
    }

    public static void logOut(SharedPreferences prefs, LoginManager loginManager) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        if (loginManager != null) {
            loginManager.logOut();
        }
    }
}
