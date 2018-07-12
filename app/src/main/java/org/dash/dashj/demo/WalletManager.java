package org.dash.dashj.demo;

import com.google.common.collect.ImmutableList;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WalletManager {

    private static WalletManager instance;

    private Set<WalletConfig> walletConfigSet;

    private WalletConfig walletConfig;

    public static void initialize(MainApplication application) {
        if (instance != null) {
            throw new IllegalStateException("WalletManager was already initialized");
        }
        instance = new WalletManager(application);
    }

    public static WalletManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WalletManager not initialized");
        }
        return instance;
    }

    private WalletManager(MainApplication application) {
        walletConfigSet = new HashSet<>();
        WalletConfig mainnetWallet = new WalletConfig(application, Constants.WALLET_MAINNET_FILENAME, MainNetParams.get());
        if (!mainnetWallet.exists()) {
            Wallet wallet = createWallet(mainnetWallet, DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH);
            mainnetWallet.create(wallet);
        }
        WalletConfig testnetWallet = new WalletConfig(application, Constants.WALLET_TESTNET3_FILENAME, TestNet3Params.get());
        if (!testnetWallet.exists()) {
            Wallet wallet = createWallet(testnetWallet, DeterministicKeyChain.BIP44_ACCOUNT_ZERO_PATH_TESTNET);
            testnetWallet.create(wallet);
        }
        WalletConfig testnetSeedWallet = new WalletConfig(application, Constants.WALLET_SEED_TESTNET3_FILENAME, TestNet3Params.get());
        if (!testnetSeedWallet.exists()) {
            String[] dummySeed = new String[]{"erode", "bridge", "organ", "you", "often", "teach", "desert", "thrive", "spike", "pottery", "sight", "sport"};
            Wallet wallet = createWallet(testnetSeedWallet, Arrays.asList(dummySeed));
            testnetSeedWallet.create(wallet);
        }

        walletConfigSet.add(mainnetWallet);
        walletConfigSet.add(testnetWallet);
        walletConfigSet.add(testnetSeedWallet);

        this.walletConfig = walletConfigSet.iterator().next();

        org.bitcoinj.core.Context.propagate(walletConfig.getWallet().getContext());
        walletConfig.getWallet().getContext().initDash(true, true);
    }

    public WalletConfig getWalletConfig() {
        return walletConfig;
    }

    private Wallet createWallet(WalletConfig walletConfig, ImmutableList<ChildNumber> bip44Path) {
        Wallet wallet = new Wallet(walletConfig.getNetworkParameters());
        wallet.addKeyChain(bip44Path);
        return wallet;
    }

    private Wallet createWallet(WalletConfig walletConfig, List<String> words) {
        DeterministicSeed deterministicSeed = new DeterministicSeed(words, null, "", Constants.EARLIEST_HD_SEED_CREATION_TIME);
        KeyChainGroup keyChainGroup = new KeyChainGroup(walletConfig.getNetworkParameters(), deterministicSeed);
        return new Wallet(walletConfig.getNetworkParameters(), keyChainGroup);
    }
}
