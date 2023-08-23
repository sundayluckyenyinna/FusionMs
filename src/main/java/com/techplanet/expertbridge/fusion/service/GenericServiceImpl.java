package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.ApiPath;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.exception.ExceptionResponse;
import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GenericServiceImpl implements GenericService
{

    private static final Gson JSON = new Gson();


    @Override
    public String postForObject(String url, String requestJson, Map<String, String> headers, Map<String, Object> params) {
        String responseJson;
        HttpResponse<String> response;
        ExceptionResponse exceptionResponse = new ExceptionResponse();

        Unirest.config().verifySsl(false);
        try{
            RequestBodyEntity requestBodyEntity = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .headers(headers)
                    .body(requestJson);
            if(params != null)
                requestBodyEntity = requestBodyEntity.queryString(params);

            response = requestBodyEntity.asString();
            if(response != null){
                responseJson = response.getBody();
                log.info("Expert Bridge Response POST: {}", responseJson);
            }else{
                exceptionResponse.setResponseCode(ResponseCode.SERVICE_UNAVAILABLE.getResponseCode());
                exceptionResponse.setResponseMessage("Expert Bridge service unavailable. Please try again.");
                responseJson = JSON.toJson(exceptionResponse);
            }
        }catch (UnirestException exception){
            log.error("Unirest Exception Message: {}", exception.getMessage());
            exceptionResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            exceptionResponse.setResponseCode(exception.getMessage());
            responseJson = JSON.toJson(exceptionResponse);
        }

        return responseJson;
    }

    @Override
    public String postForObject(String url, String requestJson, Map<String, String> headers) {
        return postForObject(url, requestJson, headers, null);
    }

    @Override
    public String postForObject(String url, String requestJson, String token) {
        Map<String, String> tokenHeader = new HashMap<>();
        token = token.replaceAll(ApiPath.TOKEN_PREFIX, Strings.EMPTY);
        token = ApiPath.TOKEN_PREFIX.concat(token);
        tokenHeader.put("Authorization", token);
        return postForObject(url, requestJson, tokenHeader);
    }
}
