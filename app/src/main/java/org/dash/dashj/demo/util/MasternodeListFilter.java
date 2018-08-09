package org.dash.dashj.demo.util;

import android.text.TextUtils;
import android.widget.Filter;

import org.bitcoinj.core.Masternode;
import org.dash.dashj.demo.WalletManager;

import java.util.ArrayList;
import java.util.List;

public abstract class MasternodeListFilter extends Filter {

    private List<Masternode> referenceMasternodeList;

    public MasternodeListFilter(List<Masternode> referenceMasternodeList) {
        this.referenceMasternodeList = referenceMasternodeList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        WalletManager.getInstance().propagateContext();
        FilterResults results = new FilterResults();
        if (!TextUtils.isEmpty(constraint)) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Masternode> filteredMasternode = new ArrayList<>();
            for (Masternode masternode : referenceMasternodeList) {
                if (masternode.getInfo().address.toString().contains(constraint)) {
                    filteredMasternode.add(masternode);
                }
            }
            results.count = filteredMasternode.size();
            results.values = filteredMasternode;
        } else {
            results.count = referenceMasternodeList.size();
            results.values = referenceMasternodeList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
        //noinspection unchecked
        publishResults((List<Masternode>) results.values);
    }

    protected abstract void publishResults(List<Masternode> masternodeFilteredList);
}