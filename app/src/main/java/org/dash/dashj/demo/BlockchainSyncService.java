package org.dash.dashj.demo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.CheckpointManager;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener;
import org.bitcoinj.net.discovery.MultiplexingDiscovery;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.net.discovery.PeerDiscoveryException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.dash.dashj.demo.event.PeerListRequestEvent;
import org.dash.dashj.demo.event.PeerListUpdateEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BlockchainSyncService extends JobService {

    private static final String TAG = BlockchainSyncService.class.getCanonicalName();

    private WalletManager walletManager;

    private BlockStore blockStore;
    private File blockChainFile;
    private BlockChain blockChain;
    private PeerGroup peerGroup;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob(" + jobParameters + ")");
        doTheJob();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob(" + jobParameters + ")");

        if (peerGroup != null) {
            Log.d(TAG, "Stopping peergroup");
            WalletManager walletManager = WalletManager.getInstance();
            peerGroup.removeDisconnectedEventListener(peerDisconnectedEventListener);
            peerGroup.removeConnectedEventListener(peerConnectedEventListener);
            peerGroup.removeWallet(walletManager.getWallet());
            peerGroup.stopAsync();
            peerGroup = null;
        }

        return false;
    }


    private void doTheJob() {
        WalletManager walletManager = WalletManager.getInstance();

        if (!walletManager.isWalletReady()) {
            Log.d(TAG, "Wallet not yet initialized");
            return;
        }

        NetworkParameters networkParameters = walletManager.getNetworkParameters();
        blockChainFile = walletManager.getBlockChainFile();
        Wallet wallet = walletManager.getWallet();

        if (!blockChainFile.exists()) {
            Log.d(TAG, "Blockchain does not exist, resetting wallet");
            wallet.reset();
        }

        initBlockChain(wallet, walletManager.getCheckpointsFileName());
        wallet.getContext().initDashSync(getDir(Constants.MASTERNODE_DIR, MODE_PRIVATE).getAbsolutePath());

        initPeerGroup(wallet);
    }

    private void initBlockChain(Wallet wallet, String checkpointAssetPath) {
        try {
            blockStore = new SPVBlockStore(wallet.getNetworkParameters(), blockChainFile);
            blockStore.getChainHead(); // detect corruptions as early as possible

            final long earliestKeyCreationTime = wallet.getEarliestKeyCreationTime();

            if (!blockChainFile.exists() && earliestKeyCreationTime > 0) {
                try {
                    final InputStream checkpointsInputStream = getAssets().open(checkpointAssetPath);
                    CheckpointManager.checkpoint(wallet.getNetworkParameters(), checkpointsInputStream, blockStore, earliestKeyCreationTime);
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
            blockChain = new BlockChain(wallet.getNetworkParameters(), wallet, blockStore);
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
                }
            });
        }
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

    private void postPeerListEvent() {
        List<Peer> connectedPeers = null;
        if (peerGroup != null) {
            connectedPeers = peerGroup.getConnectedPeers();
        }
        EventBus.getDefault().post(new PeerListUpdateEvent(connectedPeers));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
