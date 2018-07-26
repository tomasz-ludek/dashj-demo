package org.dash.dashj.demo.ui.adapter.holder;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.VersionMessage;
import org.dash.dashj.demo.R;

import java.net.InetAddress;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.peer_list_row_ip)
    TextView ipView;

    @BindView(R.id.peer_list_row_height)
    TextView heightView;

    @BindView(R.id.peer_list_row_version)
    TextView versionView;

    @BindView(R.id.peer_list_row_protocol)
    TextView protocolView;

    @BindView(R.id.peer_list_row_ping)
    TextView pingView;

    public PeerViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Peer peer) {

        final VersionMessage versionMessage = peer.getPeerVersionMessage();
        final boolean isDownloading = peer.isDownloadData();

        final InetAddress address = peer.getAddress().getAddr();
        ipView.setText(address.getHostAddress());

        final long bestHeight = peer.getBestHeight();
        heightView.setText(bestHeight > 0 ? bestHeight + " blocks" : null);
        heightView.setTypeface(isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        versionView.setText(versionMessage.subVer);
        versionView.setTypeface(isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        protocolView.setText("protocol: " + versionMessage.clientVersion);
        protocolView.setTypeface(isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        final long pingTime = peer.getPingTime();
        pingView.setText(pingTime < Long.MAX_VALUE ? getString(R.string.peer_list_row_ping_time, pingTime) : null);
        pingView.setTypeface(isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    private String getString(int resId, Object... formatArgs) {
        return itemView.getContext().getString(resId, formatArgs);
    }
}
