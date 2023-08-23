package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class ValidateElectricityResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private ElectricityValidationBillData detail;
}
