package com.techplanet.expertbridge.fusion.controller;

import com.techplanet.expertbridge.fusion.constant.ApiPath;
import com.techplanet.expertbridge.fusion.jwt.JwtTokenUtil;
import com.techplanet.expertbridge.fusion.payload.*;
import com.techplanet.expertbridge.fusion.security.AesService;
import com.techplanet.expertbridge.fusion.security.LogService;
import com.google.gson.Gson;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.HEADER_STRING;
import static com.techplanet.expertbridge.fusion.constant.ApiPath.TOKEN_PREFIX;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import static com.techplanet.expertbridge.fusion.constant.Role.SEND_SMS;
import com.techplanet.expertbridge.fusion.exception.ExceptionResponse;
import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.repository.CustomerRepository;
import com.techplanet.expertbridge.fusion.service.AirtimeDataService;
import com.techplanet.expertbridge.fusion.service.CableTvService;
import com.techplanet.expertbridge.fusion.service.ElectricityService;
import com.techplanet.expertbridge.fusion.validation.ModelValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
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
@Api(tags = "Credit Switch Service", description = "This Service is RESTFul API for BVN Validation, SMS and VAS")
@RefreshScope
public class CreditSwitchController {
    
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
    CustomerRepository customerRepository;
    
//    @Autowired
//    private CreditSwitchService creditSwitchService;
    
    @Autowired
    private AirtimeDataService airtimeDataService;
    
    @Autowired
    private ElectricityService electricityService;
     
    @Autowired
    private CableTvService cableTvService; 
     
    
    @Autowired
    private ModelValidator modelValidator;
    
//    @Value("${aes.encryption.key}")
//    private String aesEncryptionKey;
    
    private PlainTextPayload validateChannelAndRequest(String role, GenericPayload requestPayload, String token) {
        ExceptionResponse exResponse = new ExceptionResponse();
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
        return aesService.decrypt(requestPayload, appUser);
        
    }
    
//    @Operation(summary = "Send SMS to a beneficiary or a list of beneficiaries.", description = "Send SMS to a beneficiary or a list of beneficiaries.")
//    @PostMapping(value = ApiPath.SEND_SMS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> sendSms(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
//        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
//        
//        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
//        if (oValidatorPayload.isError()) {
//            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
//        } else {
//
//            // Do model validation.
//            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
//            if (validationPayload.isError()) {
//                return ResponseEntity.ok(validationPayload.getResponse());
//            }
//
//            //Valid request
//            SMSRequestPayload oSMSRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), SMSRequestPayload.class);
//            oSMSRequestPayload.setToken(token);
//            return new ResponseEntity<>(creditSwitchService.processSendSms(oSMSRequestPayload), HttpStatus.OK);
//        }
//        
//    }
    
    @Operation(summary = "Purchase airtime for a particular phone number.", description = "Purchase airtime for a particular phone number.")
    @PostMapping(value = ApiPath.AIRTIME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> airtimeTopUp(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {

            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }

            //Valid request
            AirtimeRequestPayload oAirtimeRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), AirtimeRequestPayload.class);
            oAirtimeRequestPayload.setToken(token);
            oAirtimeRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(airtimeDataService.processAirtimeRequest(token, oAirtimeRequestPayload), HttpStatus.OK);
        }
    }
    
    @Operation(summary = "Purchase data for a particular phone number.", description = "Purchase data for a particular phone number.")
    @PostMapping(value = ApiPath.DATA, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> dataTopUp(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {

            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }

            //Valid request
            DataRequestPayload oDataRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), DataRequestPayload.class);
            oDataRequestPayload.setToken(token);
            oDataRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(airtimeDataService.processDataRequest(token, oDataRequestPayload), HttpStatus.OK);
        }
    }
    
    @Operation(summary = "Fetch a list of data plans", description = "Fetch a list of data plans")
    @PostMapping(value = ApiPath.DATA_PLANS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handleDataPlansRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            DataBundlePayload oDataBundlePayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), DataBundlePayload.class);
            oDataBundlePayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(airtimeDataService.processDataPlans(oDataBundlePayload), HttpStatus.OK);
        }
    }
    
    @Operation(summary = "Validate electricity bill. This endpoint must be called and must be successful before electricity vending is performed.", description = "Validate electricity bill. This endpoint must be called and must be successful before electricity vending is performed.")
    @PostMapping(value = ApiPath.VALIDATE_ELECTRICITY_, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handleValidateElectricityRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            ElectricityBillValidationRequestPayload oElectricityBillValidationRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), ElectricityBillValidationRequestPayload.class);
            oElectricityBillValidationRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(electricityService.processValidateElectricityRequest(oElectricityBillValidationRequestPayload), HttpStatus.OK);
        }
    }
    
    @PostMapping(value = ApiPath.ELECTRICITY_BILLERS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handleElectricityBillerRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            ElectricityBillerRequestPayload oElectricityBillerRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), ElectricityBillerRequestPayload.class);
            oElectricityBillerRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(electricityService.processElectricityBillersRequest(oElectricityBillerRequestPayload), HttpStatus.OK);
        }
    }
    
    @PostMapping(value = ApiPath.PAY_ELECTRICITY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handlePayElectricityRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            ElectricityBillPaymentRequestPayload oElectricityBillPaymentRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), ElectricityBillPaymentRequestPayload.class);
            oElectricityBillPaymentRequestPayload.setToken(token);
            oElectricityBillPaymentRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(electricityService.processElectricityPayment(oElectricityBillPaymentRequestPayload), HttpStatus.OK);
        }
    }
    
    @PostMapping(value = ApiPath.VALIDATE_CABLE_BILL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handleValidateCableTvRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            CableTvValidationRequestPayload oCableTvValidationRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), CableTvValidationRequestPayload.class);
            oCableTvValidationRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(cableTvService.processCableTvValidationRequest(oCableTvValidationRequestPayload), HttpStatus.OK);
        }
    }
    
    @PostMapping(value = ApiPath.CABLE_TV_BILLERS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handleCableTvBillersRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            System.out.println(oValidatorPayload.getPlainTextPayload());
            CableTvBillerProductRequestPayload oCableTvBillerProductRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), CableTvBillerProductRequestPayload.class);
             oCableTvBillerProductRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(cableTvService.processCableTvProductRequest(oCableTvBillerProductRequestPayload), HttpStatus.OK);
        }
    }
    
    @PostMapping(value = ApiPath.PAY_CABLE_TV_BILL, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> handleCableTvPaymentRequest(@RequestBody GenericPayload requestPayload, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        
        PlainTextPayload oValidatorPayload = validateChannelAndRequest(SEND_SMS, requestPayload, token);
        if (oValidatorPayload.isError()) {
            return new ResponseEntity<>(oValidatorPayload.getResponse(), HttpStatus.OK);
        } else {
            // Do model validation.
            PlainTextPayload validationPayload = modelValidator.doModelValidation(requestPayload, oValidatorPayload);
            if (validationPayload.isError()) {
                return ResponseEntity.ok(validationPayload.getResponse());
            }
            //Valid request
            CableTvPaymentRequestPayload oCableTvPaymentRequestPayload = gson.fromJson(oValidatorPayload.getPlainTextPayload(), CableTvPaymentRequestPayload.class);
            oCableTvPaymentRequestPayload.setToken(token);
            oCableTvPaymentRequestPayload.setAppUser(oValidatorPayload.getAppUser());
            return new ResponseEntity<>(cableTvService.processCableTvPaymentRequest(oCableTvPaymentRequestPayload), HttpStatus.OK);
        }
    }
}
