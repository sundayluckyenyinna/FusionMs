package com.techplanet.expertbridge.fusion.controller;

import com.techplanet.expertbridge.fusion.jwt.JwtTokenUtil;
import com.techplanet.expertbridge.fusion.security.AesService;
import com.techplanet.expertbridge.fusion.security.LogService;
import com.google.gson.Gson;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.HEADER_STRING;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.SEND_EMAIL_URL;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.TOKEN_PREFIX;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import static com.techplanet.expertbridge.fusion.constant.Role.SEND_EMAIL;
import com.techplanet.expertbridge.fusion.exception.ExceptionResponse;
import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.payload.EmailDetailsPayload;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.PlainTextPayload;
import com.techplanet.expertbridge.fusion.repository.CustomerRepository;
import com.techplanet.expertbridge.fusion.service.EmailService;
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
@Api(tags = "Email Service", description = "This Service is RESTFul API for sending email")
@RefreshScope
public class EmailController {

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
    EmailService emailService;

    @Autowired
    CustomerRepository customerRepository;
    ExceptionResponse exResponse = new ExceptionResponse();

    @Operation(summary = "Send email")
    @PostMapping(value = SEND_EMAIL_URL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_EMAIL, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            //Valid request
            EmailDetailsPayload oEmailDetailsPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), EmailDetailsPayload.class);
            oEmailDetailsPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(emailService.sendSimpleMail(token, oEmailDetailsPayload), HttpStatus.OK);
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
