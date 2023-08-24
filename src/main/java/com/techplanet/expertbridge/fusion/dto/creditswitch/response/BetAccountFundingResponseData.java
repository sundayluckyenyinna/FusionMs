package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class BetAccountFundingResponseData
{
    private String message;
    private String name;
    private String customerId;
    private String amount;
    private String transId;
    private String date;
    private String type;
    private String internalRef;
}
