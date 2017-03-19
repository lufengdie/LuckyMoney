package com.tencent.mm.luckymoney;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;


/**
 * Created by HuangChengHua on 17/3/18.
 */

public class SettingPreference extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("wx_auto").setOnPreferenceChangeListener(this);
        findPreference("wx_private").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (preference instanceof CheckBoxPreference) {
            Settings.System.putInt(getContext().getContentResolver(), key, ((Boolean) newValue) ? 1 : 0);
            if (key.equals("wx_auto")) {
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
                    Preference p = preferenceScreen.getPreference(i);
                    if (p.getKey().equals("wx_auto")) continue;
                    p.setEnabled((Boolean) newValue);
                }
            }
        }
        return true;
    }
}
