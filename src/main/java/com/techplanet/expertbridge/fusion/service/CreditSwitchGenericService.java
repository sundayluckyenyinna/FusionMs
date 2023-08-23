package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.dto.creditswitch.response.CreditSwitchResponse;
import com.techplanet.expertbridge.fusion.model.AppUser;

import java.util.Map;

public interface CreditSwitchGenericService
{
    String computeCheckSum(Object payload);

    CreditSwitchResponse getForObject(String url, Map<String, String> headers, Map<String, Object> params);

    CreditSwitchResponse postForObject(String url, Object payload, Map<String, String> headers, Map<String, Object> params);

    String buildResponse(CreditSwitchResponse response,AppUser appUser);
}
