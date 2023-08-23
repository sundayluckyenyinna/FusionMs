package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.payload.CableTvBillerProductRequestPayload;
import com.techplanet.expertbridge.fusion.payload.CableTvPaymentRequestPayload;
import com.techplanet.expertbridge.fusion.payload.CableTvValidationRequestPayload;
import org.springframework.stereotype.Service;

@Service
public interface CableTvService
{
    String processCableTvValidationRequest(CableTvValidationRequestPayload requestPayload);
    String processCableTvProductRequest(CableTvBillerProductRequestPayload requestPayload);

    String processCableTvPaymentRequest(CableTvPaymentRequestPayload requestPayload);
}
