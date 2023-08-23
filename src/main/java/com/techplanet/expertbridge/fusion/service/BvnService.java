package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.payload.BvnRequestPayload;

/**
 *
 * @author dofoleta
 */
public interface BvnService {

    public String processBvnDetails(String token, BvnRequestPayload requestPayload);

}
