package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.dto.creditswitch.response.BetAccountValidationResponseData;
import lombok.Data;

@Data
public class BetAccountValidationResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private BetAccountValidationResponseData responseData;
    private boolean status;
}
