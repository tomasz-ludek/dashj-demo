package org.dash.dashj.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletProtobufSerializer;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WalletConfig {

    private static final String TAG = WalletConfig.class.getCanonicalName();

    private File protobufFile;
    private Wallet wallet;
    private NetworkParameters networkParameters;

    public WalletConfig(Application application, String fileName, NetworkParameters networkParameters) {
        protobufFile = application.getFileStreamPath(fileName);
        this.networkParameters = networkParameters;
    }

    public File getProtobufFile() {
        return protobufFile;
    }

    public boolean exists() {
        return protobufFile.exists();
    }

    public NetworkParameters getNetworkParameters() {
        return networkParameters;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void loadWallet(Context context) {
        if (protobufFile.exists()) {
            FileInputStream walletStream = null;
            try {
                walletStream = new FileInputStream(protobufFile);
                wallet = new WalletProtobufSerializer().readWallet(walletStream);

                if (!wallet.getParams().equals(networkParameters)) {
                    throw new UnreadableWalletException("Bad wallet network parameters: " + wallet.getParams().getId());
                }
            } catch (final FileNotFoundException | UnreadableWalletException x) {
                Log.e(TAG, "Problem loading wallet", x);
                throw new RuntimeException(x);
            } finally {
                closeSilently(walletStream);
            }

            if (!wallet.isConsistent()) {
                throw new RuntimeException(String.format("Wallet %s is not consistent", protobufFile));
            }

            if (!wallet.getParams().equals(networkParameters)) {
                throw new RuntimeException("Bad wallet network parameters: " + wallet.getParams().getId());
            }
        } else {
            throw new RuntimeException(String.format("Wallet file doesn't exist (%s)", protobufFile));
        }
    }

    public void create(Wallet wallet) {
        try {
            this.wallet = wallet;
            wallet.saveToFile(protobufFile);
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
