package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class ElectricityBillPaymentResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private ElectricityBillPaymentResponseData detail;
}
