package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.dto.Data;
import com.techplanet.expertbridge.fusion.model.Bvn;
import com.techplanet.expertbridge.fusion.payload.BvnRequestPayload;
import com.techplanet.expertbridge.fusion.payload.BvnResponsePayload;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.repository.BvnRepository;
import com.techplanet.expertbridge.fusion.security.AesService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author dofoleta
 */
@Service
public class BvnServiceImpl implements BvnService {

    @Value("${bvn.url}")
    private String bvnUrl;
    @Value("${bvn.token}")
    private String bvnToken;

    @Autowired
    Gson gson;
    @Autowired
    BvnRepository bvnRepository;
    @Autowired
    AesService aesService;

    @Override
    public String processBvnDetails(String token, BvnRequestPayload requestPayload) {
        Bvn oBvn;
        oBvn = bvnRepository.getBvnDetails(requestPayload.getBvn());
        if (oBvn != null) {
            Data data = new Data();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = "";
            try {
                formattedDate = oBvn.getBirthDate().format(dateTimeFormatter);
            } catch (Exception e) {
            }
            data.setBirthdate(formattedDate);
            data.setBvn(oBvn.getBvn());
            data.setFirstname(oBvn.getFirstName());
            data.setGender(oBvn.getGender());
//            data.setId(oBvn.getId());
            data.setLastname(oBvn.getLastName());
            data.setMiddlename(oBvn.getMiddleName());
            data.setPhone(oBvn.getMobileNumber());
            data.setPhoto(oBvn.getPhoto());

            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(data), requestPayload.getAppUser()));
            String res = gson.toJson(oGenericPayload);
            return res;
        }
        String requestBody = new JSONObject()
                .put("lastname", requestPayload.getLastName()==null?"NA":requestPayload.getLastName())
                .toString();

        String sResponse;
        Unirest.config().verifySsl(false);
        Properties props = System.getProperties();
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        HttpResponse<String> oHttpResponse = Unirest.post(bvnUrl.replace("#BVN#", requestPayload.getBvn()))
                .header("Content-Type", "application/json")
                .header("Authorization", bvnToken)
                .body(requestBody)
                .asString();
        sResponse = (String) oHttpResponse.getBody();

        BvnResponsePayload oBvnResponsePayload = gson.fromJson(sResponse, BvnResponsePayload.class);
        if (oBvnResponsePayload != null) {
            // store BVN
            oBvn = new Bvn();

            try {
                Data data = oBvnResponsePayload.getData();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(data.getBirthdate(), formatter);
                oBvn.setBirthDate(date);
                oBvn.setBvn(data.getBvn());
                oBvn.setCreatedAt(LocalDateTime.now());
                oBvn.setFirstName(data.getFirstname());
                oBvn.setGender(data.getGender());
                oBvn.setLastName(data.getLastname());
                oBvn.setMiddleName(data.getMiddlename());
                oBvn.setMobileNumber(data.getPhone());
                oBvn.setPhoto(data.getPhoto());
                bvnRepository.createBvn(oBvn);

                GenericPayload oGenericPayload = new GenericPayload();
                oGenericPayload.setResponse(aesService.encrypt(gson.toJson(data), requestPayload.getAppUser()));
                String res = gson.toJson(oGenericPayload);
                return res;
            } catch (Exception e) {
            }
            // update customer
        }
        return null;
    }

}
