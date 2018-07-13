package org.dash.dashj.demo;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import org.bitcoinj.crypto.LinuxSecureRandom;
import org.bitcoinj.utils.Threading;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

        WalletManager.initialize(this);
        scheduleBlockchainSync();
    }

    private void scheduleBlockchainSync() {
        JobInfo syncBlockchainJob = new JobInfo.Builder(SYNC_BLOCKCHAIN_JOB_ID,
                new ComponentName(this, BlockchainSyncService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
//                .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                .setMinimumLatency(1)
                .setOverrideDeadline(1)
                .build();
        JobScheduler jobScheduler =
                Objects.requireNonNull((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE));
        jobScheduler.schedule(syncBlockchainJob);
    }
}
