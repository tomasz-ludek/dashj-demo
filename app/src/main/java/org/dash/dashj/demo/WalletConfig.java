package org.dash.dashj.demo;

import android.app.Application;
import android.util.Log;

import org.bitcoinj.core.NetworkParameters;
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

    private File walletFile;
    private File blockChainFile;
    private Wallet wallet;
    private NetworkParameters networkParameters;

    public WalletConfig(Application application, String walletName, NetworkParameters networkParameters) {
        walletFile = application.getFileStreamPath(walletName + Constants.WALLET_FILE_EXT);
        blockChainFile = application.getFileStreamPath(walletName + Constants.BLOCKCHAIN_FILE_EXT);
        this.networkParameters = networkParameters;
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
        try {
            this.wallet = wallet;
            wallet.saveToFile(walletFile);
            Log.d(TAG, wallet.toString());
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
