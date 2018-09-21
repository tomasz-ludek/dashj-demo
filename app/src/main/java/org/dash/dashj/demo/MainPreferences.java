package org.dash.dashj.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.dashj.dashjinterface.config.TestNetDummyConfig;

public class MainPreferences {

    public class Contract {
        static final String KEY_LATEST_CONFIG_NAME = "key_latest_config_name";
    }

    private final SharedPreferences preferences;

    public MainPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setLatestConfigName(String value) {
        preferences.edit().putString(Contract.KEY_LATEST_CONFIG_NAME, value).apply();
    }

    public String getLatestConfigName() {
        return preferences.getString(Contract.KEY_LATEST_CONFIG_NAME, TestNetDummyConfig.NAME);
    }
}
