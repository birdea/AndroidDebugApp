package com.risewide.bdebugapp.toktok;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferenceBase {

    private static final String PREF_NAME = "pref.com.bdebug.cookie";
    private static SharedPreferences pref;


    public static void init(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }

    public static Set<String> get(String name) {
        return pref.getStringSet(name, null);
    }

    public static void put(String name, Set<String> set) {
        SharedPreferences.Editor editor = getEdit();
        editor.putStringSet(name, set);
        editor.commit();
    }

    private static SharedPreferences.Editor getEdit() {
        return pref.edit();
    }

}
