package org.dash.dashj.demo.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.Wallet;
import org.dash.dashj.demo.R;
import org.dash.dashj.demo.ui.adapter.holder.BlockViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public class BlockListAdapter extends RecyclerView.Adapter<BlockViewHolder> {

    private final Wallet wallet;
    private final LayoutInflater inflater;
    @Nullable
    private final OnClickListener onClickListener;

    private MonetaryFormat format;

    private final List<StoredBlock> blocks = new ArrayList<>();
    private Set<Transaction> transactions = new HashSet<>();

    public BlockListAdapter(final Context context, final Wallet wallet, final @Nullable OnClickListener onClickListener) {
        inflater = LayoutInflater.from(context);
        this.wallet = wallet;
        this.onClickListener = onClickListener;

        setHasStableIds(true);
    }

    public void setFormat(final MonetaryFormat format) {
        this.format = format.noCode();

        notifyDataSetChanged();
    }

    public void clear() {
        blocks.clear();

        notifyDataSetChanged();
    }

    public void replace(final Collection<StoredBlock> blocks) {
        this.blocks.clear();
        this.blocks.addAll(blocks);

        notifyDataSetChanged();
    }

    public void clearTransactions() {
        transactions.clear();
        notifyDataSetChanged();
    }

    public void replaceTransactions(final Set<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public StoredBlock getItem(final int position) {
        return blocks.get(position);
    }

    @Override
    public int getItemCount() {
        return blocks.size();
    }

    @Override
    public long getItemId(final int position) {
        return longHash(blocks.get(position).getHeader().getHash());
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new BlockViewHolder(inflater.inflate(R.layout.block_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BlockViewHolder holder, final int position) {
        final StoredBlock storedBlock = getItem(position);
        holder.bind(storedBlock, transactions, wallet);
    }


    public interface OnClickListener {
        void onBlockMenuClick(View view, StoredBlock block);
    }

//    public final boolean isDifficultyTransitionPoint(final StoredBlock storedPrev) {
//        return (storedPrev.getHeight() + 1) < 15200 ? ((storedPrev.getHeight() + 1) % Constants.NETWORK_PARAMETERS.getInterval()) == 0 : false;
//    }

    private static long longHash(final Sha256Hash hash) {
        final byte[] bytes = hash.getBytes();

        return (bytes[31] & 0xffL) | ((bytes[30] & 0xffL) << 8) | ((bytes[29] & 0xffL) << 16)
                | ((bytes[28] & 0xffL) << 24) | ((bytes[27] & 0xffL) << 32) | ((bytes[26] & 0xffL) << 40)
                | ((bytes[25] & 0xffL) << 48) | ((bytes[23] & 0xffL) << 56);
    }
}
