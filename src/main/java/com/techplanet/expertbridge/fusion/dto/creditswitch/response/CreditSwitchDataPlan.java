package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CreditSwitchDataPlan {

    private String amount;
    @SerializedName("databundle")
    private String dataBundle;
    private String validity;
    private String productId;
}
