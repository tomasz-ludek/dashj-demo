package org.dash.dashj.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.dashj.dashjinterface.config.DevNetDraConfig;
import org.dashj.dashjinterface.config.DevNetDraDummyConfig;
import org.dashj.dashjinterface.config.MainNetConfig;
import org.dashj.dashjinterface.config.TestNetConfig;
import org.dashj.dashjinterface.config.TestNetDummyConfig;

public class MainPreferences {

    public class Contract {
        static final String KEY_LATEST_CONFIG_NAME = "key_latest_config_name";
    }

    private static MainPreferences INSTANCE;
    private final SharedPreferences preferences;

    public static MainPreferences getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Not initialized");
        }
        return INSTANCE;
    }

    public static synchronized void init(Context context) {
        INSTANCE = new MainPreferences(context.getApplicationContext());
    }

    private MainPreferences(Context context) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setLatestConfigName(String value) {
        preferences.edit().putString(Contract.KEY_LATEST_CONFIG_NAME, value).apply();
    }

    public String getLatestConfigName() {
        return preferences.getString(Contract.KEY_LATEST_CONFIG_NAME, TestNetConfig.NAME);
    }
}
