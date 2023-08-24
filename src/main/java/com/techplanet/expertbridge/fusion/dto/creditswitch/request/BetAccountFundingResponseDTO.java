package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.dto.creditswitch.response.BetAccountFundingResponseData;
import lombok.Data;

@Data
public class BetAccountFundingResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private boolean status;
    private BetAccountFundingResponseData result;
}
