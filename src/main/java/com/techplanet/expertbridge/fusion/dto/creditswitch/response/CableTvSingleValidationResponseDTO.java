package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class CableTvSingleValidationResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private String balance;
    private String customerName;
    private String smartCardCode;
}
