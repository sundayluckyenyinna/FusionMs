package com.techplanet.expertbridge.fusion.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectricityBillPaymentResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private String mobileNumber;
    private String meterNumber;
    private String customerName;
    private String requestId;
    private String transRef;
    private String distributionCompany;
}
