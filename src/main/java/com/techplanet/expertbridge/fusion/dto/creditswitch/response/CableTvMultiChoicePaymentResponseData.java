package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CableTvMultiChoicePaymentResponseData
{
    private String message;
    private String amount;
    private String type;

    @SerializedName("package")
    @JsonProperty("package")
    private String packageName;

    private String transactionRef;
    private String transactionNo;
}
