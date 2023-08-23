package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.*;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.CableTvPaymentRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.CableTvProductRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.CableTvValidationRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.*;
import com.techplanet.expertbridge.fusion.payload.*;
import com.techplanet.expertbridge.fusion.security.AesService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CableTvServiceImpl implements CableTvService {

    @Value("${thirdParty.creditSwitch.loginId}")
    private String loginId;

    @Value("${thirdParty.creditSwitch.apiKey}")
    private String publicKey;

    @Value("${thirdParty.creditSwitch.privateKey}")
    private String privateKey;

    @Value("${thirdParty.creditSwitch.baseUrl}")
    private String creditSwitchBaseUrl;

    @Value("${expertBridge.accounts.cableTvPoolAccount}")
    private String cableTvPoolAccount;
    @Value("${transaction.code.bills}")
    private String billsTransactionCode;

    @Value("${expertBridge.branch.defaultBranch}")
    private String defaultBranch;

    @Autowired
    private CreditSwitchGenericService creditSwitchGenericService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FundsTransferService fundsTransferService;
    @Autowired
    AesService aesService;

    @Autowired
    Gson gson;

    @Value("${aes.encryption.key}")
    private String aesEncryptionKey;

    @Override
    public String processCableTvValidationRequest(CableTvValidationRequestPayload requestPayload) {

        OmniResponsePayload omniResponsePayload = new OmniResponsePayload();

        // Build the request DTO from client request payload to Credit Switch.
        CableTvValidationRequestDTO requestDTO = new CableTvValidationRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setKey(publicKey);
        requestDTO.setPrivateKey(privateKey);
        String serviceId = this.getServiceIdFromCableTvBiller(requestPayload.getBiller());
        if (serviceId == null) {
            omniResponsePayload.setResponseCode(ResponseCode.FAILED_MODEL.getResponseCode());
            omniResponsePayload.setResponseMessage(messageSource.getMessage("appMessages.thirdPartyRequest.cableTv.invalidBiller", null, Locale.ENGLISH));
        }
        requestDTO.setServiceId(serviceId);
        if (requestPayload.getBiller().contains(CableTvType.STAR_TIMES.name())) {
            requestDTO.setSmartCardCode(requestPayload.getSmartCard());
            requestDTO.setCustomerNo(Strings.EMPTY);
        } else {
            requestDTO.setSmartCardCode(Strings.EMPTY);
            requestDTO.setCustomerNo(requestPayload.getSmartCard());
        }
        String checksum = creditSwitchGenericService.computeCheckSum(requestDTO);
        requestDTO.setChecksum(checksum);
        requestDTO.setPrivateKey(null);

        // Submit request to Credit Switch
        String url = this.getCableTvValidationUrlFromCableTvBiller(requestPayload.getBiller());
        CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
        if (response.isError()) {
            return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
        }

        // Build the response to the client.
        CableTvValidationResponsePayload responsePayload = new CableTvValidationResponsePayload();
        if (requestPayload.getBiller().toUpperCase().contains(CableTvType.STAR_TIMES.name())) {
            CableTvSingleValidationResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), CableTvSingleValidationResponseDTO.class);
            responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
            responsePayload.setResponseMessage(messageSource.getMessage("appMessages.request.success", null, Locale.ENGLISH));
            responsePayload.setCardholderName(responseDTO.getCustomerName());
            responsePayload.setOtherCustomerInfo(String.join(StringValues.SPACE_STROKE, responseDTO.getBalance(), responseDTO.getSmartCardCode()));
        } else {
            CableTvMultiChoiceValidationResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), CableTvMultiChoiceValidationResponseDTO.class);
            responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
            responsePayload.setResponseMessage(messageSource.getMessage("appMessages.request.success", null, Locale.ENGLISH));
            CableTvMultiChoiceValidationResponseData data = responseDTO.getStatusDescription();
            responsePayload.setCardholderName(String.join(StringValues.SPACE_COMMA, data.getFirstname(), data.getLastname()));
            responsePayload.setOtherCustomerInfo(String.join(StringValues.SPACE_STROKE, String.valueOf(data.getAmount()), data.getCustomerNo()));
            responsePayload.setInvoicePeriod(data.getInvoicePeriod());
            responsePayload.setDueDate(data.getDueDate());
        }

        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processCableTvProductRequest(CableTvBillerProductRequestPayload requestPayload) {
        OmniResponsePayload omniResponsePayload = new OmniResponsePayload();

        // Build the request from client request payload
        CableTvProductRequestDTO requestDTO = new CableTvProductRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setKey(publicKey);
        if (requestPayload.getBiller().toUpperCase().contains(CableTvType.STAR_TIMES.name())) {
            omniResponsePayload.setResponseCode(ResponseCode.FAILED_MODEL.getResponseCode());
            omniResponsePayload.setResponseMessage(messageSource.getMessage("appMessages.thirdPartyRequest.cableTv.invalidBillerForProduct", null, Locale.ENGLISH));
            return gson.toJson(omniResponsePayload);
        }
        String serviceId = this.getServiceIdFromCableTvBiller(requestPayload.getBiller());
        if (serviceId == null) {
            omniResponsePayload.setResponseCode(ResponseCode.FAILED_MODEL.getResponseCode());
            omniResponsePayload.setResponseMessage(messageSource.getMessage("appMessages.thirdPartyRequest.cableTv.invalidBiller", null, Locale.ENGLISH));
        }

        requestDTO.setServiceId(serviceId);

        // Submit request to Credit Switch
        String url = creditSwitchBaseUrl.concat(ApiPath.CS_CABLE_TV_PRODUCT);
        CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
        if (response.isError()) {
            //TDDO revers debit
            return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
        }

        // Build the response payload.
        CableTvProductResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), CableTvProductResponseDTO.class);
        CableTvProductResponsePayload responsePayload = new CableTvProductResponsePayload();
        responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        responsePayload.setResponseMessage(messageSource.getMessage("appMessages.request.success", null, Locale.ENGLISH));
        AtomicInteger counter = new AtomicInteger(0);
        List<CableTvProductResponseData> billers = responseDTO.getStatusDescription().getItems()
                .stream()
                .map(item -> {
                    int totalMonths = item.getAvailablePricingOptions().size();
                    double perMonthPrice = item.getAvailablePricingOptions().get(0).getPrice();
                    CableTvProductResponseData biller = new CableTvProductResponseData();
                    biller.setBillerId(requestDTO.getServiceId());
                    biller.setBiller(requestPayload.getBiller().toUpperCase());
                    biller.setId(String.valueOf(counter.incrementAndGet()));
                    biller.setProductId(item.getCode());
                    biller.setAmount(String.valueOf(perMonthPrice * totalMonths));
                    biller.setMonthlyAmount(String.valueOf(perMonthPrice));
                    biller.setPackageName(item.getName());
                    biller.setBouquet(item.getDescription());
                    biller.setYearlyAmount(String.valueOf(perMonthPrice * 12));
                    biller.setStatus(ModelStatus.OK.name());
                    biller.setVendor(BillPaymentVendor.CREDIT_SWITCH.name());
                    biller.setAvailableMonths(totalMonths);
                    return biller;
                }).collect(Collectors.toList());

        responsePayload.setBillers(billers);

        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processCableTvPaymentRequest(CableTvPaymentRequestPayload requestPayload) {

        OmniResponsePayload omniResponsePayload = new OmniResponsePayload();

        // Build the request payload to Credit switch
        CableTvPaymentRequestDTO requestDTO = new CableTvPaymentRequestDTO();
        String billerId = requestPayload.getBillerId().toUpperCase();
        requestDTO.setLoginId(loginId);
        requestDTO.setKey(publicKey);
        requestDTO.setPrivateKey(privateKey);

        String url;
        if (billerId.contains(CableTvType.STAR_TIMES.name())) {
            requestDTO.setSmartCardCode(requestPayload.getSmartCard());
            requestDTO.setFee(requestPayload.getAmount());
            requestDTO.setTransactionRef(requestPayload.getRequestId());
            url = creditSwitchBaseUrl.concat(ApiPath.CS_SINGLE_CABLE_TV_PAYMENT);
        } else if (billerId.contains(CableTvType.DSTV.name()) || billerId.contains(CableTvType.GOTV.name())) {
            requestDTO.setServiceId(this.getServiceIdFromCableTvBiller(billerId));
            requestDTO.setTransactionRef(requestPayload.getRequestId());
            requestDTO.setCustomerNo(requestPayload.getSmartCard());
            requestDTO.setCustomerName(requestPayload.getCustomerName());
            requestDTO.setProductsCodes(requestPayload.getProductId());
            requestDTO.setAmount(requestPayload.getAmount());
            requestDTO.setInvoicePeriod(requestPayload.getInvoicePeriod());
            url = creditSwitchBaseUrl.concat(ApiPath.CS_MULTI_CHOICE_CABLE_TV_PAYMENT);
        } else {
            omniResponsePayload.setResponseCode(ResponseCode.FAILED_MODEL.getResponseCode());
            omniResponsePayload.setResponseMessage(messageSource.getMessage("appMessages.thirdPartyRequest.cableTv.invalidBiller", null, Locale.ENGLISH));
            // apply encryption here
            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniResponsePayload), requestPayload.getAppUser()));
            return gson.toJson(oGenericPayload);
        }

        requestDTO.setChecksum(creditSwitchGenericService.computeCheckSum(requestDTO));
        requestDTO.setPrivateKey(null);

        // Debit the customer for the transaction.
        FundsTransferPayload fundsTransferPayload = new FundsTransferPayload();
        fundsTransferPayload.setBeneficiaryMobileNumber(requestPayload.getSmartCard());
        fundsTransferPayload.setGateway("CreditSwitch");
        fundsTransferPayload.setCategory("BILL/CABLETV");
        fundsTransferPayload.setNarration(billerId.concat("/").concat("CABLETV SUBSCRIPTION FOR ").concat(requestPayload.getSmartCard()));

        fundsTransferPayload.setAmount(requestPayload.getAmount());
        fundsTransferPayload.setBeneficiaryName("VAS POOL ACCOUNT");
        fundsTransferPayload.setBranchCode(defaultBranch);
        fundsTransferPayload.setChannelName(requestPayload.getChannelName() == null ? "USSD" : requestPayload.getChannelName());
        fundsTransferPayload.setCreditAccount(cableTvPoolAccount);
        fundsTransferPayload.setCurrencyCode(requestPayload.getCurrencyCode() == null ? "NGN" : requestPayload.getCurrencyCode());
        fundsTransferPayload.setDebitAccount(requestPayload.getDebitAccount());
        fundsTransferPayload.setMobileNumber(formatMobileNumber(requestPayload.getMobileNumber()));
        fundsTransferPayload.setPin(requestPayload.getPin());
        fundsTransferPayload.setRequestId(requestPayload.getRequestId());
        fundsTransferPayload.setTransactionType(billsTransactionCode);
        fundsTransferPayload.setTransRef(requestPayload.getRequestId());
        
        fundsTransferPayload.setOriginatorName(requestPayload.getCustomerName());

        fundsTransferPayload.setDeviceId(requestPayload.getDeviceId());

        
        fundsTransferPayload.setChannelName(requestPayload.getChannelName());

        String fundsTransferRequestJson = gson.toJson(fundsTransferPayload);
        String fundsTransferResponseJsonPost = fundsTransferService.localTransfer(requestPayload.getToken(), fundsTransferRequestJson);

        // Check the status of funds-transfer to provide value to customer.
        FundsTransferResponsePayload fundsTransferResponsePayload = gson.fromJson(fundsTransferResponseJsonPost, FundsTransferResponsePayload.class);
        String responseCode = fundsTransferResponsePayload.getResponseCode();

        if (responseCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
            CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
            if (response.isError()) {
                return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
            }

            CableTvPaymentResponsePayload responsePayload = new CableTvPaymentResponsePayload();
            responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
            responsePayload.setResponseMessage(messageSource.getMessage("appMessages.request.success", null, Locale.ENGLISH));

            if (billerId.contains(CableTvType.STAR_TIMES.name())) {
                CableTvSinglePaymentResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), CableTvSinglePaymentResponseDTO.class);
                responsePayload.setUserAcct(requestPayload.getDebitAccount());
                responsePayload.setTransRef(responseDTO.getTransactionNo());
            } else {
                CableTvMultiChoicePaymentResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), CableTvMultiChoicePaymentResponseDTO.class);
                responsePayload.setUserAcct(requestPayload.getDebitAccount());
                responsePayload.setTransRef(responseDTO.getStatusDescription().getTransactionNo());
            }

            // apply encryption here
            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
            return gson.toJson(oGenericPayload);
        }

        // Here funds transfer failed. Return error to the client.
        omniResponsePayload.setResponseCode(responseCode);
        omniResponsePayload.setResponseMessage(fundsTransferResponsePayload.getResponseMessage());
        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniResponsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }


    public String formatMobileNumber(String mobileNumber) {
        if (mobileNumber != null && mobileNumber.startsWith("234")) {
            mobileNumber = mobileNumber.replace(mobileNumber.substring(0, 3), "0");
        }
        return mobileNumber;
    }
 
    private String getServiceIdFromCableTvBiller(@NotNull String biller) {
        biller = biller.toUpperCase().trim();
        if (biller.contains(CableTvType.STAR_TIMES.name())) {
            return CableTvType.STAR_TIMES.getServiceCode();
        } else if (biller.contains(CableTvType.DSTV.name())) {
            return CableTvType.DSTV.getServiceCode();
        } else if (biller.contains(CableTvType.GOTV.name())) {
            return CableTvType.GOTV.getServiceCode();
        }
        return null;
    }

    private String getCableTvValidationUrlFromCableTvBiller(@NotNull String biller) {
        biller = biller.trim().toUpperCase();
        if (biller.contains(CableTvType.STAR_TIMES.name())) {
            return creditSwitchBaseUrl.concat(ApiPath.CS_VALIDATE_SINGLE_CHOICE_CABLE);
        } else if (biller.contains(CableTvType.DSTV.name()) || biller.contains(CableTvType.GOTV.name())) {
            return creditSwitchBaseUrl.concat(ApiPath.CS_VALIDATE_MULTI_CHOICE_CABLE);
        }
        return null;
    }
}
