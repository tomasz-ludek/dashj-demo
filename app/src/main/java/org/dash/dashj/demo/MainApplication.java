package org.dash.dashj.demo;

import android.app.Application;

import org.bitcoinj.utils.Threading;
import org.dashj.dashjinterface.WalletAppKitService;
import org.dashj.dashjinterface.config.DevNetDraConfig;
import org.dashj.dashjinterface.config.MainNetConfig;
import org.dashj.dashjinterface.config.TestNetConfig;
import org.dashj.dashjinterface.config.TestNetDummyConfig;
import org.dashj.dashjinterface.config.WalletConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getCanonicalName();

    public static Map<String, WalletConfig> walletConfigMap = new LinkedHashMap<>();

    static {
        walletConfigMap.put(MainNetConfig.NAME, MainNetConfig.get());
        walletConfigMap.put(TestNetConfig.NAME, TestNetConfig.get());
        walletConfigMap.put(DevNetDraConfig.NAME, DevNetDraConfig.get());
//        walletConfigMap.put(TestNetDummyConfig.NAME, TestNetDummyConfig.get());
        walletConfigMap.put(DevNetDraDerp.NAME, DevNetDraDerp.get());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();

        Threading.throwOnLockCycles();
        org.bitcoinj.core.Context.enableStrictMode();

        MainPreferences.init(this);
        activateConfig(MainPreferences.getInstance().getLatestConfigName());
    }

    private void activateConfig(final String walletName) {
        if (walletConfigMap.containsKey(walletName)) {
            WalletConfig walletConfig = walletConfigMap.get(walletName);
            WalletAppKitService.init(this, walletConfig);
        } else {
            throw new IllegalArgumentException("Unknown wallet " + walletName);
        }
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

    static class DevNetDraDerp extends org.dashj.dashjinterface.config.WalletConfig {

        static String NAME = "derp (devnet-DRA)";

        private static DevNetDraDerp INSTANCE = new DevNetDraDerp(
                NAME,
                Network.DEVNET_DRA,
                "devnet-dra-derp",
                null,
                Constants.DUMMY_SEED);

        static DevNetDraDerp get() {
            return INSTANCE;
        }

        public DevNetDraDerp(String _name, Network _network, String _filesPrefix, String _checkpointsAssetPath, List<String> _seed) {
            super(_name, _network, _filesPrefix, _checkpointsAssetPath, _seed);
        }
    }
}
