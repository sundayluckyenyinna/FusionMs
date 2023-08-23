package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class SmsResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private double cost;
    private Long tranxReference;
    private String transactionId;
}
