package org.dash.dashj.demo;

import android.app.Application;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import org.bitcoinj.crypto.LinuxSecureRandom;
import org.bitcoinj.utils.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getCanonicalName();

    private static final int SYNC_BLOCKCHAIN_JOB_ID = 0x0100;

    @Override
    public void onCreate() {
        super.onCreate();

//        initLogging();
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

    private void initLogging() {
        final File logDir = getDir("log", MODE_PRIVATE);
        final File logFile = new File(logDir, "wallet.log");

        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        final PatternLayoutEncoder filePattern = new PatternLayoutEncoder();
        filePattern.setContext(context);
        filePattern.setPattern("%d{HH:mm:ss,UTC} [%thread] %logger{0} - %msg%n");
        filePattern.start();

        final RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
        fileAppender.setContext(context);
        fileAppender.setFile(logFile.getAbsolutePath());

        final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(logDir.getAbsolutePath() + "/wallet.%d{yyyy-MM-dd,UTC}.log.gz");
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.start();

        fileAppender.setEncoder(filePattern);
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();

        final PatternLayoutEncoder logcatTagPattern = new PatternLayoutEncoder();
        logcatTagPattern.setContext(context);
        logcatTagPattern.setPattern("%logger{0}");
        logcatTagPattern.start();

        final PatternLayoutEncoder logcatPattern = new PatternLayoutEncoder();
        logcatPattern.setContext(context);
        logcatPattern.setPattern("[%thread] %msg%n");
        logcatPattern.start();

        final LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(context);
        logcatAppender.setTagEncoder(logcatTagPattern);
        logcatAppender.setEncoder(logcatPattern);
        logcatAppender.start();

        final ch.qos.logback.classic.Logger log = context.getLogger(Logger.ROOT_LOGGER_NAME);
        log.addAppender(fileAppender);
        log.addAppender(logcatAppender);
        log.setLevel(Level.INFO);
    }
}
