package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.dto.DestinationDTO;
import com.techplanet.expertbridge.fusion.dto.MessageDTO;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.InfoBipSmsPayload;
import com.techplanet.expertbridge.fusion.payload.SMSRequestPayload;
import com.techplanet.expertbridge.fusion.payload.SMSResponsePayload;
import com.techplanet.expertbridge.fusion.security.AesService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    AesService aesService;

    @Autowired
    Gson gson;

    @Value("${sms.gateway.key}")
    private String smsApiKey;

    @Value("${sms.sender.id}")
    private String smsSenderId;

    @Override
    public String processSendSms(String token, InfoBipSmsPayload oInfoBipSmsPayload) {
        SMSResponsePayload oSMSResponsePayload = new SMSResponsePayload();
        if (oInfoBipSmsPayload != null) {
            List destinationDTOList = new ArrayList<>();
            DestinationDTO oDestinationDTO = new DestinationDTO();
            oDestinationDTO.setTo(formatMobileNumber(oInfoBipSmsPayload.getRecipient()));
            destinationDTOList.add(oDestinationDTO);

            List messageDTOList = new ArrayList<>();

            MessageDTO oMessageDTO = new MessageDTO();
            oMessageDTO.setDestinations(destinationDTOList);
            oMessageDTO.setFrom(smsSenderId);
            oMessageDTO.setText(oInfoBipSmsPayload.getTextMessage());
            messageDTOList.add(oMessageDTO);
            SMSRequestPayload oSMSRequestPayload = new SMSRequestPayload();
            oSMSRequestPayload.setMessages(messageDTOList);

            String requestBody = gson.toJson(oSMSRequestPayload);

            String sResponse;
            Unirest.config().verifySsl(false);
            Properties props = System.getProperties();
            props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

//            HttpResponse<String> oHttpResponse = Unirest.post(smsUrl)
            HttpResponse<String> oHttpResponse = Unirest.post("http://yd3n9.api.infobip.com/sms/2/text/advanced")
                    .header("Content-Type", "application/json")
                    .header("Authorization", smsApiKey)
                    .body(requestBody)
                    .asString();
            sResponse = (String) oHttpResponse.getBody();

            if (oHttpResponse.getStatus() == 200 && sResponse != null && !sResponse.isEmpty()) {
                oSMSResponsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
                oSMSResponsePayload.setResponseMessage("Successful");
            }

        } else {
            oSMSResponsePayload.setResponseCode(ResponseCode.SYSTEM_ERROR.getResponseCode());
            oSMSResponsePayload.setResponseMessage("Failed");
        }
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(oSMSResponsePayload), oInfoBipSmsPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    public String formatMobileNumber(String mobileNumber) {
        mobileNumber = mobileNumber.replaceAll("\\+", "");
        if (mobileNumber != null && !mobileNumber.startsWith("234")) {
            mobileNumber = mobileNumber.replaceFirst(mobileNumber.substring(0,1), "234");
        }
        return mobileNumber;
    }
}
