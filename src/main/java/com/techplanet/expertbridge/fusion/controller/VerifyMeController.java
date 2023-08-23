package com.techplanet.expertbridge.fusion.controller;

import com.techplanet.expertbridge.fusion.jwt.JwtTokenUtil;
import com.techplanet.expertbridge.fusion.security.AesService;
import com.techplanet.expertbridge.fusion.security.LogService;
import com.google.gson.Gson;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.BVN_DETAILS;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.HEADER_STRING;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.TOKEN_PREFIX;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import static com.techplanet.expertbridge.fusion.constant.Role.BVN_ENQ;
import com.techplanet.expertbridge.fusion.exception.ExceptionResponse;
import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.payload.BvnRequestPayload;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.PlainTextPayload;
import com.techplanet.expertbridge.fusion.repository.CustomerRepository;
import com.techplanet.expertbridge.fusion.service.BvnService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Daniel Ofoleta
 */

@RestController
@Api(tags = "VerifyMe Service", description = "This Service is RESTFul API for BVN, NIN and other IDs Validation")
@RefreshScope
public class VerifyMeController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    LogService logService;
    @Autowired
    Gson gson;
    @Autowired
    JwtTokenUtil jwtToken;
    @Autowired
    AesService aesService;

    @Autowired
    BvnService bvnService;
    
    @Autowired
    CustomerRepository customerRepository;

    ExceptionResponse exResponse = new ExceptionResponse();

    @Operation(summary = "Fetch BVN details")
    @PostMapping(value = BVN_DETAILS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> bvnDetails(@Valid @RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(BVN_ENQ, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            //Valid request
            BvnRequestPayload oBvnRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), BvnRequestPayload.class);
            oBvnRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(bvnService.processBvnDetails(token, oBvnRequestPayload), HttpStatus.OK);
        }
    }

    private PlainTextPayload validateChannelAndRequest(String role, GenericPayload requestPayload, String token) {
        
        boolean userHasRole = jwtToken.userHasRole(token, role);
        String requestBy = jwtToken.getUsernameFromToken(token);
        AppUser appUser = customerRepository.getAppUserUsingUsername(requestBy);
        if (!userHasRole) {
            exResponse.setResponseCode(ResponseCode.NO_ROLE.getResponseCode());
            exResponse.setResponseMessage(messageSource.getMessage("appMessages.request.norole", new Object[0], Locale.ENGLISH));
            String exceptionJson = gson.toJson(exResponse);
            logService.logInfo("", token, messageSource.getMessage("appMessages.user.hasnorole", new Object[]{0}, Locale.ENGLISH), "API Response", exceptionJson);
            PlainTextPayload validatorPayload = new PlainTextPayload();
            validatorPayload.setError(true);
            GenericPayload responsePayload = new GenericPayload();
            responsePayload.setResponse(aesService.encrypt(exceptionJson, appUser));
            validatorPayload.setResponse(gson.toJson(responsePayload));
            return validatorPayload;
        }
        return aesService.decrypt(requestPayload,appUser);
    }
}
