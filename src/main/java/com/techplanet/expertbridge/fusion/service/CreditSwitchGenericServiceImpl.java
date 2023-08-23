package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.constant.StringValues;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.CreditSwitchGenericResponse;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.CreditSwitchResponse;
import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.OmniResponsePayload;
import com.techplanet.expertbridge.fusion.repository.CustomerRepository;
import com.techplanet.expertbridge.fusion.security.AesService;
import com.techplanet.expertbridge.fusion.security.LogService;
import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditSwitchGenericServiceImpl implements CreditSwitchGenericService {

    @Autowired
    private LogService logService;

    @Autowired
    AesService aesService;

    @Autowired
    Gson gson;

    @Override
    public String computeCheckSum(Object payload) {
        // Use Java reflection to introspect and build the concat string.
        List<Field> fields = List.of(payload.getClass().getDeclaredFields());
        List<String> stringValues = new ArrayList<>();
        fields = fields.stream()
                .filter(field -> !field.getName().equalsIgnoreCase("checksum"))
                .filter(field -> field.isAnnotationPresent(CheckSumInclude.class))
                .sorted(Comparator.comparingInt(a -> a.getAnnotation(CheckSumInclude.class).order()))
                .collect(Collectors.toList());
        fields.forEach(field -> {
            field.setAccessible(true);
            try {
                Object fieldValueObject = field.get(payload);
                if (fieldValueObject != null) {
                    String fieldValue = fieldValueObject.toString();
                    if (fieldValue != null && !fieldValue.isEmpty() && !fieldValue.isBlank() && !fieldValue.equalsIgnoreCase("null")) {
                        stringValues.add(fieldValue);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        String concatString = String.join(StringValues.STROKE, stringValues);
        System.out.println(concatString);

        // Return Base64 encoded value of the Bcrypt hash of the concat string.
        byte[] bytes = BCrypt.hashpw(concatString, BCrypt.gensalt()).getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public CreditSwitchResponse getForObject(String url, Map<String, String> headers, Map<String, Object> params) {
        String responseJsonGet;
        HttpResponse<String> response;
        CreditSwitchResponse creditSwitchResponse = new CreditSwitchResponse();
        CreditSwitchGenericResponse genericResponse;
        Unirest.config().verifySsl(false);
        try {
            GetRequest getRequest = Unirest.get(url).header("Content-Type", "application/json");
            if (headers != null) {
                getRequest = getRequest.headers(headers);
            }
            if (params != null) {
                getRequest = getRequest.queryString(params);
            }
            response = getRequest.asString();
            responseJsonGet = response.getBody();

            // Service is available and CreditSwitch responds well.
            logService.logInfo("", "", "", "INFO", "");
            log.info("ResponseJsonGet: {}", responseJsonGet);
            if (!responseJsonGet.isBlank() && !responseJsonGet.isEmpty()) {
                genericResponse = gson.fromJson(responseJsonGet, CreditSwitchGenericResponse.class);
                String statusCode = genericResponse.getStatusCode();
                if (statusCode != null && statusCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
                    creditSwitchResponse.setError(false);
                    creditSwitchResponse.setErrorResponse(null);
                    creditSwitchResponse.setSuccessResponse(responseJsonGet);
                } else {
                    creditSwitchResponse.setError(true);
                    creditSwitchResponse.setErrorResponse(responseJsonGet);
                    creditSwitchResponse.setSuccessResponse(null);
                }
                return creditSwitchResponse;
            }

            // Here the CreditSwitch service is unavailable.
            logService.logInfo("", "", "", "INFO", "");
            log.info("Service unavailable due to status: {}", (response.getStatus() + response.getStatusText()));
            genericResponse = new CreditSwitchGenericResponse();
            genericResponse.setStatusCode(ResponseCode.SERVICE_UNAVAILABLE.getResponseCode());
            genericResponse.setStatusDescription("");
            responseJsonGet = gson.toJson(genericResponse);
            creditSwitchResponse.setError(true);
            creditSwitchResponse.setErrorResponse(responseJsonGet);
            creditSwitchResponse.setSuccessResponse(null);
            return creditSwitchResponse;
        } catch (UnirestException exception) {
            logService.logError("", "", exception.getMessage(), "ERROR", "");
            log.debug("Exception Response: {}", exception.getMessage());
            genericResponse = new CreditSwitchGenericResponse();
            genericResponse.setStatusCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            genericResponse.setStatusDescription(exception.getMessage());
            responseJsonGet = gson.toJson(genericResponse);
            creditSwitchResponse.setError(true);
            creditSwitchResponse.setErrorResponse(responseJsonGet);
            creditSwitchResponse.setSuccessResponse(null);
            return creditSwitchResponse;
        }
    }

    @Override
    public CreditSwitchResponse postForObject(String url, Object payload, Map<String, String> headers, Map<String, Object> params) {
        String responseJsonPost;
        HttpResponse<String> response;
        CreditSwitchResponse creditSwitchResponse = new CreditSwitchResponse();
        CreditSwitchGenericResponse genericResponse;
        Unirest.config().verifySsl(false);
        try {
            RequestBodyEntity bodyRequest = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(payload));
            if (headers != null) {
                bodyRequest = bodyRequest.headers(headers);
            }
            if (params != null) {
                bodyRequest = bodyRequest.queryString(params);
            }
            response = bodyRequest.asString();
            responseJsonPost = response.getBody();

            // Service is available and CreditSwitch responds well.
            logService.logInfo("", "", "", "INFO", "");
            log.info("ResponseJsonPost: {}", responseJsonPost);
            if (!responseJsonPost.isBlank() && !responseJsonPost.isEmpty()) {
                genericResponse = gson.fromJson(responseJsonPost, CreditSwitchGenericResponse.class);
                String statusCode = genericResponse.getStatusCode();
                if (statusCode != null && statusCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
                    creditSwitchResponse.setError(false);
                    creditSwitchResponse.setErrorResponse(null);
                    creditSwitchResponse.setSuccessResponse(responseJsonPost);
                } else {
                    creditSwitchResponse.setError(true);
                    creditSwitchResponse.setErrorResponse(responseJsonPost);
                    creditSwitchResponse.setSuccessResponse(null);
                }
                return creditSwitchResponse;
            }

            // Here the CreditSwitch service is unavailable.
            logService.logInfo("", "", "", "INFO", "");
            log.info("Service unavailable due to status: {}", (response.getStatus() + response.getStatusText()));
            genericResponse = new CreditSwitchGenericResponse();
            genericResponse.setStatusCode(ResponseCode.SERVICE_UNAVAILABLE.getResponseCode());
            genericResponse.setStatusDescription("");
            responseJsonPost = gson.toJson(genericResponse);
            creditSwitchResponse.setError(true);
            creditSwitchResponse.setErrorResponse(responseJsonPost);
            creditSwitchResponse.setSuccessResponse(null);
            return creditSwitchResponse;
        } catch (UnirestException exception) {
            logService.logError("", "", exception.getMessage(), "ERROR", "");
            log.debug("Exception Response: {}", exception.getMessage());
            genericResponse = new CreditSwitchGenericResponse();
            genericResponse.setStatusCode(ResponseCode.INTERNAL_SERVER_ERROR.getResponseCode());
            genericResponse.setStatusDescription(exception.getMessage());
            responseJsonPost = gson.toJson(genericResponse);
            creditSwitchResponse.setError(true);
            creditSwitchResponse.setErrorResponse(responseJsonPost);
            creditSwitchResponse.setSuccessResponse(null);
            return creditSwitchResponse;
        }
    }

    @Override
    public String buildResponse(CreditSwitchResponse response, AppUser appUser) {
        OmniResponsePayload omniResponsePayload = new OmniResponsePayload();
        CreditSwitchGenericResponse genericResponse = gson.fromJson(response.getErrorResponse(), CreditSwitchGenericResponse.class);
        omniResponsePayload.setResponseCode(genericResponse.getStatusCode());
        omniResponsePayload.setResponseMessage(genericResponse.getStatusDescription().toString());
        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniResponsePayload), appUser));
        return gson.toJson(oGenericPayload);
    }
}
