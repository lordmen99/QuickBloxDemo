package com.raghav.quickbloxdemo.support;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by raghav.satyadev on 18/7/16.
 */
public class PrefsHelper {

    private SharedPreferences sharedPreferences;

    public PrefsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(Const.Preference.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }


    public String getEmail() {
        return sharedPreferences.getString(Const.Preference.email, null);
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString(Const.Preference.email, email).commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(Const.Preference.password, null);
    }

    public void setPassword(String password) {
        sharedPreferences.edit().putString(Const.Preference.password, password).commit();
    }

    public Set<String> getTags() {
        return sharedPreferences.getStringSet(Const.Preference.Tags, null);
    }

    public void setTags(Set<String> stringSet) {
        sharedPreferences.edit().putStringSet(Const.Preference.Tags, stringSet).commit();
    }
}
