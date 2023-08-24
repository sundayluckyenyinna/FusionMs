package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.dto.creditswitch.response.BetAccountFundingResponseData;
import lombok.Data;

@Data
public class BetAccountFundingResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private BetAccountFundingResponseData responseData;
}
