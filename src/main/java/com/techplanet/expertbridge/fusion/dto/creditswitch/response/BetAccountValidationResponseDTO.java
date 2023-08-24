package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class BetAccountValidationResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private BetAccountValidationResponseData result;
    private boolean status;
}
