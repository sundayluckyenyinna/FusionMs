package com.techplanet.expertbridge.fusion.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirtimeDataResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private String transactionRef;
    private String userAcct;
}
