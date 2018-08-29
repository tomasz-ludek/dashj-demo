package org.dash.dashj.demo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.CheckpointManager;
import org.bitcoinj.core.MasternodeSync;
import org.bitcoinj.core.MasternodeSyncListener;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.SporkMessage;
import org.bitcoinj.core.SporkSyncListener;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener;
import org.bitcoinj.governance.GovernanceObject;
import org.bitcoinj.governance.listeners.GovernanceObjectAddedEventListener;
import org.bitcoinj.net.discovery.MultiplexingDiscovery;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.net.discovery.PeerDiscoveryException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.dash.dashj.demo.event.BlockListRequestEvent;
import org.dash.dashj.demo.event.BlockListUpdateEvent;
import org.dash.dashj.demo.event.MasternodeListUpdateEvent;
import org.dash.dashj.demo.event.PeerListRequestEvent;
import org.dash.dashj.demo.event.PeerListUpdateEvent;
import org.dash.dashj.demo.event.SporkListRequestEvent;
import org.dash.dashj.demo.event.SporkListUpdateEvent;
import org.dash.dashj.demo.event.SyncUpdateEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BlockchainSyncService extends Service {

    private static final String TAG = BlockchainSyncService.class.getCanonicalName();

    private WalletManager walletManager;

    private BlockStore blockStore;
    private File blockChainFile;
    private BlockChain blockChain;
    private PeerGroup peerGroup;

    @Override
    public void onCreate() {
        super.onCreate();
        walletManager = WalletManager.getInstance();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doTheJob();
        return START_NOT_STICKY;
    }

    private void doTheJob() {

        if (!walletManager.isWalletReady()) {
            Log.d(TAG, "Wallet not yet initialized");
            stopSelf();
            return;
        }

        blockChainFile = walletManager.getBlockChainFile();
        final Wallet wallet = walletManager.getWallet();

        if (!blockChainFile.exists()) {
            Log.d(TAG, "Blockchain does not exist, resetting wallet");
            wallet.reset();
        }

        initBlockChain(wallet, walletManager.getCheckpointsFileName());
        wallet.getContext().initDashSync(walletManager.getMasternodeDataPath());

        initPeerGroup(wallet);

        wallet.getContext().sporkManager.addEventListener(new SporkSyncListener() {
            @Override
            public void onUpdate(SporkMessage spork) {
                postSporkListEvent(spork);
            }
        });

        wallet.getContext().governanceManager.addGovernanceObjectAddedEventListener(new GovernanceObjectAddedEventListener() {
            @Override
            public void onGovernanceObjectAdded(Sha256Hash nHash, GovernanceObject object) {
                Log.d(TAG, "onGovernanceObjectAdded: " + object);
            }
        });

        wallet.getContext().masternodeSync.addEventListener(new MasternodeSyncListener() {
            @Override
            public void onSyncStatusChanged(int newStatus, double syncStatus) {
                postMasternodeListEvent(newStatus);
                Log.d(TAG, "newStatus: " + newStatus);
                if (newStatus == MasternodeSync.MASTERNODE_SYNC_FINISHED) {

                }
            }
        });

        postBlockListEvent();
    }

    private void initBlockChain(Wallet wallet, String checkpointAssetPath) {
        NetworkParameters networkParameters = wallet.getNetworkParameters();
        try {
            blockStore = new SPVBlockStore(networkParameters, blockChainFile);
            blockStore.getChainHead(); // detect corruptions as early as possible

            final long earliestKeyCreationTime = wallet.getEarliestKeyCreationTime();

            if (!blockChainFile.exists() && earliestKeyCreationTime > 0) {
                try {
                    final InputStream checkpointsInputStream = getAssets().open(checkpointAssetPath);
                    CheckpointManager.checkpoint(networkParameters, checkpointsInputStream, blockStore, earliestKeyCreationTime);
                } catch (final IOException x) {
                    Log.w(TAG, "Problem reading checkpoints, continuing without", x);
                }
            }
        } catch (final BlockStoreException x) {
            //noinspection ResultOfMethodCallIgnored
            blockChainFile.delete();
            throw new RuntimeException("Blockstore cannot be created", x);
        }

        try {
            blockChain = new BlockChain(networkParameters, wallet, blockStore);
        } catch (final BlockStoreException x) {
            throw new RuntimeException("Blockchain cannot be created", x);
        }
    }

    private void initPeerGroup(final Wallet wallet) {
        if (peerGroup == null) {

            // consistency check
            final int walletLastBlockSeenHeight = wallet.getLastBlockSeenHeight();
            final int bestChainHeight = blockChain.getBestChainHeight();
            if (walletLastBlockSeenHeight != -1 && walletLastBlockSeenHeight != bestChainHeight) {
                Log.w(TAG, "Wallet/Blockchain out of sync: " + walletLastBlockSeenHeight + "/" + bestChainHeight);
            }

            Log.d(TAG, "Starting PeerGroup");
            peerGroup = new PeerGroup(wallet.getNetworkParameters(), blockChain);
            peerGroup.setDownloadTxDependencies(0); // recursive implementation causes StackOverflowError
            peerGroup.addWallet(wallet);
            peerGroup.setUserAgent(getString(R.string.app_name), Utils.packageInfo(this).versionName);

            peerGroup.setMaxConnections(Constants.MAX_CONNECTED_PEERS);
            peerGroup.setConnectTimeoutMillis(Constants.PEER_TIMEOUT_MS);
            peerGroup.setPeerDiscoveryTimeoutMillis(Constants.PEER_DISCOVERY_TIMEOUT_MS);

            peerGroup.addConnectedEventListener(peerConnectedEventListener);
            peerGroup.addDisconnectedEventListener(peerDisconnectedEventListener);

            peerGroup.addPeerDiscovery(new PeerDiscovery() {

                private final PeerDiscovery normalPeerDiscovery =
                        MultiplexingDiscovery.forServices(wallet.getNetworkParameters(), 0);

                @Override
                public InetSocketAddress[] getPeers(final long services, final long timeoutValue, final TimeUnit timeoutUnit)
                        throws PeerDiscoveryException {
                    final List<InetSocketAddress> peers = new LinkedList<>();

                    peers.addAll(Arrays.asList(normalPeerDiscovery.getPeers(services, timeoutValue, timeoutUnit)));
                    return peers.toArray(new InetSocketAddress[0]);
                }

                @Override
                public void shutdown() {
                    normalPeerDiscovery.shutdown();
                }
            });

            // start peergroup
            peerGroup.startAsync();
            peerGroup.startBlockChainDownload(new DownloadProgressTracker() {
                @Override
                protected void progress(double pct, int blocksSoFar, Date date) {
                    Log.d(TAG, String.format(Locale.US, "Chain download %d%% done with %d blocks to go, block date %s", (int) pct, blocksSoFar, Utils.dateTimeFormat(date)));
                    EventBus.getDefault().postSticky(new SyncUpdateEvent(pct, blocksSoFar, date));
                    postBlockListEvent();
                }
            });
        }

        postPeerListEvent();
    }

    private PeerConnectedEventListener peerConnectedEventListener = new PeerConnectedEventListener() {
        @Override
        public void onPeerConnected(Peer peer, int peerCount) {
            Log.d(TAG, String.format("onPeerConnected(%s, %d)", peer.toString(), peerCount));
            postPeerListEvent();
        }
    };

    private PeerDisconnectedEventListener peerDisconnectedEventListener = new PeerDisconnectedEventListener() {
        @Override
        public void onPeerDisconnected(Peer peer, int peerCount) {
            Log.d(TAG, String.format("onPeerDisconnected(%s, %d)", peer.toString(), peerCount));
            postPeerListEvent();
        }
    };

    @Subscribe
    public void onPeerListRequestEvent(PeerListRequestEvent event) {
        postPeerListEvent();
    }

    @Subscribe
    public void onSporkListRequestEvent(SporkListRequestEvent event) {
        postSporkListEvent(null);
    }

    @Subscribe
    public void onBlockListRequestEvent(BlockListRequestEvent event) {
        postBlockListEvent();
    }

    private void postPeerListEvent() {
        List<Peer> connectedPeers = null;
        if (peerGroup != null) {
            connectedPeers = peerGroup.getConnectedPeers();
        }
        EventBus.getDefault().post(new PeerListUpdateEvent(connectedPeers));
    }

    private void postSporkListEvent(SporkMessage spork) {
        EventBus.getDefault().post(new SporkListUpdateEvent(spork));
    }

    @SuppressLint("StaticFieldLeak")
    private void postBlockListEvent() {
        new AsyncTask<Void, Void, List<StoredBlock>>() {
            @Override
            protected List<StoredBlock> doInBackground(Void... voids) {
                int maxBlocks = Constants.MAX_BLOCKS;
                final List<StoredBlock> blocks = new ArrayList<>(maxBlocks);
                try {
                    StoredBlock block = blockChain.getChainHead();
                    while (block != null) {
                        blocks.add(block);
                        if (blocks.size() >= maxBlocks) {
                            break;
                        }
                        block = block.getPrev(blockStore);
                    }
                } catch (final BlockStoreException ignored) {
                }
                return blocks;
            }

            @Override
            protected void onPostExecute(List<StoredBlock> storedBlocks) {
                EventBus.getDefault().post(new BlockListUpdateEvent(storedBlocks));
            }
        }.execute();
    }

    private void postMasternodeListEvent(int syncStatus) {
        EventBus.getDefault().post(new MasternodeListUpdateEvent(syncStatus));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        Wallet wallet = walletManager.getWallet();
//        wallet.removeChangeEventListener(walletEventListener);
//        wallet.removeCoinsSentEventListener(walletEventListener);
//        wallet.removeCoinsReceivedEventListener(walletEventListener);

        if (peerGroup != null) {
            Log.d(TAG, "Stopping peergroup");
            peerGroup.removeDisconnectedEventListener(peerDisconnectedEventListener);
            peerGroup.removeConnectedEventListener(peerConnectedEventListener);
            peerGroup.removeWallet(wallet);
            peerGroup.stopAsync();
            peerGroup = null;
        }

        try {
            blockStore.close();
        } catch (final BlockStoreException x) {
            throw new RuntimeException(x);
        }

        walletManager.saveWallet();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
