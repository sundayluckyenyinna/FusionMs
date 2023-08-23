package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class ElectricityBillPaymentResponseData
{
    private String name;
    private String address;
    private String accountId;
    private String amount;
    private String units;
    private String token;
    private String tranxId;
}
