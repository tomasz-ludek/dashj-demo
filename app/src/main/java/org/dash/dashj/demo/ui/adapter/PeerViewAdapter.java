package org.dash.dashj.demo.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitcoinj.core.Peer;
import org.dash.dashj.demo.R;
import org.dash.dashj.demo.ui.adapter.holder.PeerViewHolder;

import java.util.LinkedList;
import java.util.List;

public class PeerViewAdapter extends RecyclerView.Adapter<PeerViewHolder> {

    private final LayoutInflater inflater;
    private final List<Peer> peers = new LinkedList<>();

    public PeerViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    public void clear() {
        peers.clear();
        notifyDataSetChanged();
    }

    public void replace(final List<Peer> peers) {
        this.peers.clear();
        this.peers.addAll(peers);

        notifyDataSetChanged();
    }

    public Peer getItem(final int position) {
        return peers.get(position);
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    @Override
    public long getItemId(final int position) {
        return peers.get(position).getAddress().hashCode();
    }

    @NonNull
    @Override
    public PeerViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View itemView = inflater.inflate(R.layout.peer_list_row, parent, false);
        return new PeerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PeerViewHolder holder, final int position) {
        Peer peer = getItem(position);
        holder.bind(peer);
    }
}