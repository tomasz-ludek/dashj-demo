package org.dash.dashj.demo;

import android.content.Intent;
import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.DevNetParams;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;
import org.dash.dashj.demo.event.WalletReloadEvent;
import org.dashj.dashjinterface.WalletAppKitService;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WalletManager {

    private static WalletManager instance;

    private Map<String, WalletConfig> walletConfigMap;

    private WalletConfig walletConfig;

    public static void initialize(MainApplication application, String walletName) {
        if (instance != null) {
            throw new IllegalStateException("WalletManager was already initialized");
        }
        instance = new WalletManager(application, walletName);
    }

    public static WalletManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WalletManager not initialized");
        }
        return instance;
    }

    private WalletManager(MainApplication application, String walletName) {
        walletConfigMap = new LinkedHashMap<>();
        WalletConfig mainnetWallet = new WalletConfig(application, Constants.WALLET_MAINNET_NAME, MainNetParams.get());
        if (!mainnetWallet.exists()) {
            Wallet wallet = createWallet(mainnetWallet, DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH);
            mainnetWallet.create(wallet);
        }
        walletConfigMap.put(Constants.WALLET_MAINNET_NAME, mainnetWallet);
        WalletConfig testnetWallet = new WalletConfig(application, Constants.WALLET_TESTNET3_NAME, TestNet3Params.get());
        if (!testnetWallet.exists()) {
            Wallet wallet = createWallet(testnetWallet, DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH_TESTNET);
            testnetWallet.create(wallet);
        }
        walletConfigMap.put(Constants.WALLET_TESTNET3_NAME, testnetWallet);
        WalletConfig testnetSeedWallet = new WalletConfig(application, Constants.WALLET_SEED_TESTNET3_NAME, TestNet3Params.get());
        if (!testnetSeedWallet.exists()) {
            String[] dummySeed = new String[]{"erode", "bridge", "organ", "you", "often", "teach", "desert", "thrive", "spike", "pottery", "sight", "sport"};
            Wallet wallet = createWallet(testnetSeedWallet, Arrays.asList(dummySeed));
            testnetSeedWallet.create(wallet);
        }
        walletConfigMap.put(Constants.WALLET_SEED_TESTNET3_NAME, testnetSeedWallet);

        String[] dnsSeeds = {"54.255.164.83", "52.77.231.13", "13.250.14.191"};
        DevNetParams draDevNetParams = DevNetParams.get("DRA", "yPhZ6EKNntpLDZBovHd1xAYjfwYmrBMT5N", 12999, dnsSeeds);
        WalletConfig draDevnetSeedWallet = new WalletConfig(application, Constants.WALLET_SEED_DEVNET_DRA_NAME, draDevNetParams);
        if (!draDevnetSeedWallet.exists()) {
            String[] dummySeed = new String[]{"erode", "bridge", "organ", "you", "often", "teach", "desert", "thrive", "spike", "pottery", "sight", "sport"};
            Wallet wallet = createWallet(draDevnetSeedWallet, Arrays.asList(dummySeed));
            draDevnetSeedWallet.create(wallet);
        }
        walletConfigMap.put(Constants.WALLET_SEED_DEVNET_DRA_NAME, draDevnetSeedWallet);

        setActiveWallet(walletName, application);
        Log.d("FreshReceiveAddress", walletConfig.getWallet().freshReceiveAddress().toBase58());
        Log.d("FreshReceiveAddress", walletConfig.getWallet().toString(true, true, true, null));
    }

    public void setActiveWallet(String walletName, android.content.Context context) {
        if (walletConfigMap.containsKey(walletName)) {
            WalletConfig walletConfig = walletConfigMap.get(walletName);
            setActiveWallet(walletConfig, context);
        } else {
            throw new IllegalArgumentException("Unknown wallet " + walletName);
        }
    }

    private void setActiveWallet(WalletConfig newWalletConfig, android.content.Context context) {
        Intent blockchainSyncServiceIntent = new Intent(context, WalletAppKitService.class);
        if (walletConfig != null) {
            walletConfig.saveWallet();
//            Wallet wallet = walletConfig.getWallet();
//            wallet.removeChangeEventListener(walletEventListener);
//            wallet.removeCoinsSentEventListener(walletEventListener);
//            wallet.removeCoinsReceivedEventListener(walletEventListener);
            context.stopService(blockchainSyncServiceIntent);
        }
        walletConfig = newWalletConfig;
        Wallet wallet = walletConfig.getWallet();
        if (wallet != null) {
            propagateContext();
        } else {
            walletConfig.loadWallet();
            wallet = walletConfig.getWallet();
        }
//        wallet.addChangeEventListener(walletEventListener);
//        wallet.addCoinsSentEventListener(walletEventListener);
//        wallet.addCoinsReceivedEventListener(walletEventListener);
        wallet.getContext().initDash(false, true);
        context.startService(blockchainSyncServiceIntent);

        EventBus.getDefault().post(new WalletReloadEvent());
    }

    public boolean isWalletReady() {
        return walletConfig.getWallet() != null;
    }

    public void propagateContext() {
        org.bitcoinj.core.Context.propagate(getWallet().getContext());
    }

    public Wallet getWallet() {
        return walletConfig.getWallet();
    }

    public void saveWallet() {
        walletConfig.saveWallet();
    }

    public File getBlockChainFile() {
        return walletConfig.getBlockChainFile();
    }

    public NetworkParameters getNetworkParameters() {
        return walletConfig.getNetworkParameters();
    }

    public String getCheckpointsFileName() {
        return walletConfig.getCheckpointsFilePath();
    }

    public String getMasternodeDataPath() {
        return walletConfig.getMasternodeDataPath();
    }

    public String getConfigName() {
        return walletConfig.getNamingPrefix();
    }

    private Wallet createWallet(WalletConfig walletConfig, ImmutableList<ChildNumber> bip44Path) {
        NetworkParameters networkParameters = walletConfig.getNetworkParameters();
        org.bitcoinj.core.Context.propagate(new Context(networkParameters));
        Wallet wallet = new Wallet(networkParameters);
        wallet.addKeyChain(bip44Path);
        return wallet;
    }

    private Wallet createWallet(WalletConfig walletConfig, List<String> words) {
        NetworkParameters networkParameters = walletConfig.getNetworkParameters();
        org.bitcoinj.core.Context.propagate(new Context(networkParameters));
        DeterministicSeed deterministicSeed = new DeterministicSeed(words, null, "", Constants.EARLIEST_HD_SEED_CREATION_TIME);
        KeyChainGroup keyChainGroup = new KeyChainGroup(networkParameters, deterministicSeed);
        return new Wallet(walletConfig.getNetworkParameters(), keyChainGroup);
    }
}
