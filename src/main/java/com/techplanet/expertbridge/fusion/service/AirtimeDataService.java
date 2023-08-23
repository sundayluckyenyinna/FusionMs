package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.payload.AirtimeRequestPayload;
import com.techplanet.expertbridge.fusion.payload.DataBundlePayload;
import com.techplanet.expertbridge.fusion.payload.DataRequestPayload;
import org.springframework.stereotype.Service;

@Service
public interface AirtimeDataService {

    public String processAirtimeRequest(String token, AirtimeRequestPayload requestPayload);

    public String processDataRequest(String token, DataRequestPayload requestPayload);

    public String processDataPlans(DataBundlePayload oDataBundlePayload);
}
