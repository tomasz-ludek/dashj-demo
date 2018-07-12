package org.dash.dashj.demo;

import android.app.Application;

import org.bitcoinj.crypto.LinuxSecureRandom;
import org.bitcoinj.utils.Threading;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new LinuxSecureRandom(); // init proper random number generator

        Threading.throwOnLockCycles();
        org.bitcoinj.core.Context.enableStrictMode();

        WalletManager.initialize(this);
    }
}
