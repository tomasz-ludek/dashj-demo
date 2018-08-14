package org.dash.dashj.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.common.base.Stopwatch;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletProtobufSerializer;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WalletConfig {

    private static final String TAG = WalletConfig.class.getCanonicalName();

    private String namingPrefix;
    private File walletFile;
    private File blockChainFile;
    private File masternodeDataDir;
    private Wallet wallet;
    private NetworkParameters networkParameters;

    public WalletConfig(Application application, String namingPrefix, NetworkParameters networkParameters) {
        this.namingPrefix = namingPrefix;
        walletFile = application.getFileStreamPath(namingPrefix + Constants.WALLET_FILE_EXT);
        blockChainFile = application.getFileStreamPath(namingPrefix + Constants.BLOCKCHAIN_FILE_EXT);
        masternodeDataDir = application.getDir(namingPrefix + Constants.MASTERNODE_DATA_DIR_EXT, Context.MODE_PRIVATE);
        this.networkParameters = networkParameters;
    }

    public String getWalletFilePath() {
        return walletFile.getAbsolutePath();
    }

    public String getBlockChainFilePath() {
        return blockChainFile.getAbsolutePath();
    }

    public String getMasternodeDataPath() {
        return masternodeDataDir.getAbsolutePath();
    }

    public String getCheckpointsFilePath() {
        return namingPrefix + Constants.CHECKPOINTS_FILE_EXT;
    }

    public File getWalletFile() {
        return walletFile;
    }

    public File getBlockChainFile() {
        return blockChainFile;
    }

    public boolean exists() {
        return walletFile.exists();
    }

    public File getMasternodeCacheFile() {
        return new File(getMasternodeDataPath(), Constants.MASTERNODE_CACHE_FILE_NAME);
    }

    public File getGovernanceCacheFile() {
        return new File(getMasternodeDataPath(), Constants.GOVERNANCE_CACHE_FILE_NAME);
    }

    public String getNamingPrefix() {
        return namingPrefix;
    }

    public NetworkParameters getNetworkParameters() {
        return networkParameters;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void loadWallet() {
        if (walletFile.exists()) {
            FileInputStream walletStream = null;
            try {
                walletStream = new FileInputStream(walletFile);
                org.bitcoinj.core.Context.propagate(new org.bitcoinj.core.Context(networkParameters));
                wallet = new WalletProtobufSerializer().readWallet(walletStream);

                if (!wallet.getParams().equals(networkParameters)) {
                    throw new UnreadableWalletException("Bad wallet network parameters: " + wallet.getParams().getId());
                }
            } catch (final FileNotFoundException | UnreadableWalletException x) {
                Log.e(TAG, "Problem loading wallet", x);
//                KeyChainGroup keyChainGroup = new KeyChainGroup(networkParameters);
//                wallet = new Wallet(networkParameters, keyChainGroup);
////                public static final ImmutableList<ChildNumber> BIP44_PATH = TEST ? DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH_TESTNET : DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH;
//                wallet.addKeyChain(DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH);
//                Log.e(TAG, "wallet.toString(): " + wallet.toString(true, true, true, null));
                throw new RuntimeException(String.format("Wallet %s is not consistent", walletFile));
            } finally {
                closeSilently(walletStream);
            }

            if (!wallet.isConsistent()) {
                throw new RuntimeException(String.format("Wallet %s is not consistent", walletFile));
            }

            if (!wallet.getParams().equals(networkParameters)) {
                throw new RuntimeException("Bad wallet network parameters: " + wallet.getParams().getId());
            }
        } else {
            throw new RuntimeException(String.format("Wallet file doesn't exist (%s)", walletFile));
        }
    }

    public void create(Wallet wallet) {
        this.wallet = wallet;
        wallet.autosaveToFile(walletFile, 30, TimeUnit.SECONDS, null);
        saveWallet();
    }

    public void saveWallet() {
        try {
            final Stopwatch watch = Stopwatch.createStarted();
            wallet.saveToFile(walletFile);
            watch.stop();
            Log.i(TAG, String.format("Wallet saved to: '%s', took %s", walletFile, watch));
        } catch (final IOException x) {
            throw new RuntimeException(x);
        }
    }

    private void closeSilently(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (final IOException x) {
            // swallow
        }
    }
}
