package org.dash.dashj.demo.util;

import android.util.Pair;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.dash.dashj.demo.ui.governancelist.GovernanceProposalData;
import org.json.JSONArray;
import org.json.JSONException;

public class GovernanceHelper {

    public static GovernanceProposalData parseProposal(String proposalData) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GovernanceProposalData> adapter = moshi.adapter(GovernanceProposalData.class);
        try {
            Pair<String, String> typeAndData = extractTypeAndData(proposalData);
            return adapter.fromJson(typeAndData.second);
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect proposal data " + proposalData, e);
        }
    }

    private static Pair<String, String> extractTypeAndData(String data) throws JSONException {
        JSONArray enclosureArray = new JSONArray(data).getJSONArray(0);
        return new Pair<>(enclosureArray.getString(0), enclosureArray.getString(1));
    }
}
