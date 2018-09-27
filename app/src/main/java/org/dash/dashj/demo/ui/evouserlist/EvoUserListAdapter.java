package org.dash.dashj.demo.ui.evouserlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.bitcoinj.evolution.EvolutionUser;
import org.dash.dashj.demo.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EvoUserListAdapter extends RecyclerView.Adapter<EvoUserListViewHolder> {

    private final LayoutInflater inflater;
    private final List<EvolutionUser> evoUserList = new ArrayList<>();
    private final OnOptionClickListener listener;

    public EvoUserListAdapter(final Context context, OnOptionClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;

        setHasStableIds(true);
    }

    public void clear() {
        evoUserList.clear();

        notifyDataSetChanged();
    }

    public void replace(final Collection<EvolutionUser> evoUsers) {
        this.evoUserList.clear();
        this.evoUserList.addAll(evoUsers);

        notifyDataSetChanged();
    }

    public EvolutionUser getItem(final int position) {
        return evoUserList.get(position);
    }

    @Override
    public int getItemCount() {
        return evoUserList.size();
    }

    @Override
    public long getItemId(final int position) {
        return evoUserList.get(position).getRegTxId().toBigInteger().longValue();
    }

    @NonNull
    @Override
    public EvoUserListViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new EvoUserListViewHolder(inflater.inflate(R.layout.evo_user_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final EvoUserListViewHolder holder, final int position) {
        final EvolutionUser evoUser = getItem(position);
        holder.bind(evoUser, listener);
    }

    public interface OnOptionClickListener {

        void onTopUp(EvolutionUser evoUser);

        void onReset(EvolutionUser evoUser);
    }
}
