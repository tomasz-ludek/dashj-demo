package org.dash.dashj.demo.ui.addresshierarchy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.dash.dashj.demo.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddressHierarchyAdapter extends RecyclerView.Adapter<AddressHierarchyViewHolder> {

    private final LayoutInflater inflater;
    private final List<ECKey> addressList = new ArrayList<>();
    private NetworkParameters networkParameters;

    public AddressHierarchyAdapter(final Context context) {
        inflater = LayoutInflater.from(context);

        setHasStableIds(true);
    }

    public void clear() {
        addressList.clear();

        notifyDataSetChanged();
    }

    public void replace(final Collection<ECKey> ecKeys, NetworkParameters networkParameters) {
        this.addressList.clear();
        this.addressList.addAll(ecKeys);
        this.networkParameters = networkParameters;

        notifyDataSetChanged();
    }

    public ECKey getItem(final int position) {
        return addressList.get(position);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    @Override
    public long getItemId(final int position) {
        return addressList.get(position).hashCode();
    }

    @NonNull
    @Override
    public AddressHierarchyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new AddressHierarchyViewHolder(inflater.inflate(R.layout.addresses_hierarchy_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AddressHierarchyViewHolder holder, final int position) {
        final ECKey key = getItem(position);
        holder.bind(key, networkParameters);
    }
}
