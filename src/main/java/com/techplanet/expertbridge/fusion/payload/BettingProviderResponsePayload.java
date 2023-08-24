package com.techplanet.expertbridge.fusion.payload;

import lombok.Data;

import java.util.List;

@Data
public class BettingProviderResponsePayload {

    private String responseCode;
    private String responseMessage;
    private List<BettingProviderData> responseData;
}
