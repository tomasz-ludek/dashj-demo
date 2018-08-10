package org.dash.dashj.demo.ui.governancelist;

import android.text.TextUtils;
import android.util.Pair;
import android.widget.Filter;

import org.bitcoinj.governance.GovernanceObject;
import org.dash.dashj.demo.WalletManager;
import org.dash.dashj.demo.util.GovernanceHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class GovernanceListFilter extends Filter {

    private List<GovernanceObject> refGovernanceObjects;

    public GovernanceListFilter(List<GovernanceObject> refGovernanceObjects) {
        this.refGovernanceObjects = refGovernanceObjects;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        WalletManager.getInstance().propagateContext();
        FilterResults results = new FilterResults();
        if (!TextUtils.isEmpty(constraint)) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<GovernanceObject> filteredGovernanceObjects = new ArrayList<>();
            for (GovernanceObject governanceObject : refGovernanceObjects) {
                Pair<String, String> typeAndData = GovernanceHelper.extractTypeAndData(governanceObject.getDataAsPlainString());
                if (typeAndData.first.equals("proposal") && typeAndData.second.toUpperCase().contains(constraint)) {
                    filteredGovernanceObjects.add(governanceObject);
                }
            }
            results.count = filteredGovernanceObjects.size();
            results.values = filteredGovernanceObjects;
        } else {
            results.count = refGovernanceObjects.size();
            results.values = refGovernanceObjects;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //noinspection unchecked
        publishResults((List<GovernanceObject>) results.values);
    }

    protected abstract void publishResults(List<GovernanceObject> masternodeFilteredList);
}