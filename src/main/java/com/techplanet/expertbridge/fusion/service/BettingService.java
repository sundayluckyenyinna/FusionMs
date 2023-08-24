package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.payload.BetAccountFundingRequestPayload;
import com.techplanet.expertbridge.fusion.payload.BetAccountValidationRequestPayload;
import com.techplanet.expertbridge.fusion.payload.BettingProvidersRequestPayload;

public interface BettingService {
    String processBettingProvidersListRequest(BettingProvidersRequestPayload requestPayload);

    String processBetAccountValidation(BetAccountValidationRequestPayload requestPayload);

    String processBetAccountFunding(BetAccountFundingRequestPayload requestPayload);
}
