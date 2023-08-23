package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class CableTvMultiChoiceValidationResponseData
{
    private String customerNo;
    private String firstname;
    private String lastname;
    private String customerType;
    private String invoicePeriod;
    private String dueDate;
    private double amount;
}
