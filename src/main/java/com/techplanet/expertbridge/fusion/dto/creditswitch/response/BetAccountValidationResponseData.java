package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class BetAccountValidationResponseData
{
    private String status;
    private String message;
    private String name;
    private String username;
    private String type;
    private String customerId;
    private String reference;
    private String accountNumber;
    private String phoneNumber;
    private String emailAddress;
    private String canVend;
    private String minPayableAmount;
    private String charge;
}
