package org.dash.dashj.demo.util;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.bitcoinj.governance.GovernanceObject;
import org.dash.dashj.demo.ui.governancelist.GovernanceProposalData;
import org.json.JSONArray;
import org.json.JSONException;

public class GovernanceHelper {

    public static GovernanceProposalData parseProposal(GovernanceObject governanceObject) {
        String data = extractData(governanceObject.getDataAsPlainString());
        return parseProposal(data);
    }

    public static GovernanceProposalData parseProposal(String proposalData) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GovernanceProposalData> adapter = moshi.adapter(GovernanceProposalData.class);
        try {
            return adapter.fromJson(proposalData);
        } catch (Exception e) {
            throw new IllegalArgumentException("Incorrect proposal data " + proposalData, e);
        }
    }

    public static String extractData(String data) {
        try {
            JSONArray enclosureArray = new JSONArray(data).getJSONArray(0);
            return enclosureArray.getString(1);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Incorrect proposal data " + data, e);
        }
    }
}
