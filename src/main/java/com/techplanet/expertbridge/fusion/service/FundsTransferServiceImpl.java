package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.jwt.JwtTokenUtil;
import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.repository.CustomerRepository;
import com.techplanet.expertbridge.fusion.security.AesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FundsTransferServiceImpl implements FundsTransferService
{

    @Value("${expertBridge.baseUrl}")
    private String expertBridgeBaseUrl;

    @Autowired
    private GenericService genericService;

    @Autowired
    private AesService aesService;
    
    @Autowired
    private JwtTokenUtil jwtToken;

    @Autowired
    private CustomerRepository customerRepository;

    private static final Gson JSON = new Gson();


    @Override
    public String localTransfer(String token, String requestPayload) {
        String requestBy = jwtToken.getUsernameFromToken(token);
        AppUser appUser = customerRepository.getAppUserUsingUsername(requestBy);
        String encryptedJson = aesService.encrypt(requestPayload, appUser);
        GenericPayload requestGenericPayload = new GenericPayload();
        requestGenericPayload.setRequest(encryptedJson);
        String genericRequestJson = JSON.toJson(requestGenericPayload);

        String url = expertBridgeBaseUrl.concat("/genericService/ft/local");
        String genericResponseJson = genericService.postForObject(url, genericRequestJson, token);
        GenericPayload genericResponsePayload = JSON.fromJson(genericResponseJson, GenericPayload.class);
        return aesService.decrypt(genericResponsePayload.getResponse(), appUser);
    }

}
