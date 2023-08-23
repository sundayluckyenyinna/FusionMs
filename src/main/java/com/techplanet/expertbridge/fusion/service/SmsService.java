package com.techplanet.expertbridge.fusion.service;

import com.techplanet.expertbridge.fusion.payload.InfoBipSmsPayload;
import org.springframework.stereotype.Service;

@Service
public interface SmsService
{
    String processSendSms(String token, InfoBipSmsPayload oInfoBipSmsPayload);
}
