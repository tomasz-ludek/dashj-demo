package org.dash.dashj.demo;

import java.util.concurrent.TimeUnit;

public interface Constants {

    String WALLET_TESTNET3_NAME = "testnet";
    String WALLET_SEED_TESTNET3_NAME = "seed-testnet";
    String WALLET_MAINNET_NAME = "mainnet";

    String WALLET_FILE_EXT = ".wallet";
    String BLOCKCHAIN_FILE_EXT = ".blockchain";

    String CHECKPOINTS_MAINNET_FILENAME = "checkpoints-mainnet.txt";
    String CHECKPOINTS_TESTNET_FILENAME = "checkpoints-testnet.txt";

    String MASTERNODE_DIR = "masternode";

    long EARLIEST_HD_SEED_CREATION_TIME = 1427610960L;

    int MAX_CONNECTED_PEERS = 6;
    int PEER_DISCOVERY_TIMEOUT_MS = (int) TimeUnit.MINUTES.toMillis(10);
    int PEER_TIMEOUT_MS = (int) TimeUnit.MINUTES.toMillis(15);

    int MAX_BLOCKS = 128;
}
