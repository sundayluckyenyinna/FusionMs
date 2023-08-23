package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.payload.ElectricityBillPaymentRequestPayload;
import com.techplanet.expertbridge.fusion.payload.ElectricityBillValidationRequestPayload;
import com.techplanet.expertbridge.fusion.payload.ElectricityBillerRequestPayload;
import org.springframework.stereotype.Service;

@Service
public interface ElectricityService
{
    String processValidateElectricityRequest(ElectricityBillValidationRequestPayload requestPayload);

    String processElectricityBillersRequest(ElectricityBillerRequestPayload requestPayload);

    String processElectricityPayment(ElectricityBillPaymentRequestPayload requestPayload);
}
