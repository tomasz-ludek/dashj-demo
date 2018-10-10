package org.dash.dashj.demo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface Constants {

    List<String> DUMMY_SEED = Arrays.asList("corn", "trophy", "crumble", "burger", "attend", "reopen", "pelican", "useless", "bicycle", "universe", "giggle", "alpha");

    String MASTERNODE_CACHE_FILE_NAME = "mncache.dat";
    String GOVERNANCE_CACHE_FILE_NAME = "goverance.dat";

    long EARLIEST_HD_SEED_CREATION_TIME = 1427610960L;

    int MAX_CONNECTED_PEERS = 6;
    int PEER_DISCOVERY_TIMEOUT_MS = (int) TimeUnit.MINUTES.toMillis(10);
    int PEER_TIMEOUT_MS = (int) TimeUnit.MINUTES.toMillis(15);

    int MAX_BLOCKS = 4096;
}
