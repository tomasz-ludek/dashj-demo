package org.dash.dashj.demo.util;

import android.util.Pair;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.dash.dashj.demo.ui.governancelist.GovernanceProposalData;
import org.json.JSONArray;

import java.io.IOException;

public class GovernanceHelper {

    public static GovernanceProposalData parseProposal(String proposalData) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GovernanceProposalData> adapter = moshi.adapter(GovernanceProposalData.class);
        try {
            return adapter.fromJson(proposalData);
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect proposal data " + proposalData, e);
        }
    }

    public static Pair<String, String> extractTypeAndData(String data) {
        try {
            JSONArray enclosureArray = new JSONArray(data).getJSONArray(0);
            return new Pair<>(enclosureArray.getString(0), enclosureArray.getString(1));
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect proposal data " + data, e);
        }
    }
}
