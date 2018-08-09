package org.dash.dashj.demo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.bitcoinj.crypto.LinuxSecureRandom;
import org.bitcoinj.utils.Threading;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getCanonicalName();

    private static final int SYNC_BLOCKCHAIN_JOB_ID = 0x0100;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, getString(R.string.app_name) + " started");

        new LinuxSecureRandom(); // init proper random number generator

        Threading.throwOnLockCycles();
        org.bitcoinj.core.Context.enableStrictMode();

        MainPreferences preferences = new MainPreferences(this);
        WalletManager.initialize(this, preferences.getLatestConfigName());
//        scheduleBlockchainSync();
    }

    private void scheduleBlockchainSync() {
        Intent blockchainSyncServiceIntent = new Intent(this, BlockchainSyncService.class);
        startService(blockchainSyncServiceIntent);
//        JobInfo syncBlockchainJob = new JobInfo.Builder(SYNC_BLOCKCHAIN_JOB_ID,
//                new ComponentName(this, BlockchainSyncService.class))
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setPersisted(true)
////                .setPeriodic(TimeUnit.MINUTES.toMillis(1))
//                .setMinimumLatency(1)
//                .setOverrideDeadline(1)
//                .build();
//        JobScheduler jobScheduler =
//                Objects.requireNonNull((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE));
//        jobScheduler.schedule(syncBlockchainJob);
    }
}
