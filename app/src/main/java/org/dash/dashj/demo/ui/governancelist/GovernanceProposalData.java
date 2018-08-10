package org.dash.dashj.demo.ui.governancelist;

import com.squareup.moshi.Json;

public class GovernanceProposalData {

    @Json(name = "name")
    public String name;

    @Json(name = "payment_address")
    public String paymentAddress;

    @Json(name = "payment_amount")
    public float paymentAmount;

    @Json(name = "start_epoch")
    public long startDate;

    @Json(name = "end_epoch")
    public long endDate;

    @Json(name = "url")
    public String url;
}
