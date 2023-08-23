package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.*;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.AirtimeRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.DataPlanRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.DataRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.AirtimeDataResponseDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.CreditSwitchResponse;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.DataPlanResponseDTO;
import com.techplanet.expertbridge.fusion.payload.*;
import com.techplanet.expertbridge.fusion.security.AesService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AirtimeDataServiceImpl implements AirtimeDataService {

    @Value("${thirdParty.creditSwitch.loginId}")
    private String loginId;

    @Value("${thirdParty.creditSwitch.apiKey}")
    private String publicKey;

    @Value("${thirdParty.creditSwitch.privateKey}")
    private String privateKey;

    @Value("${thirdParty.creditSwitch.baseUrl}")
    private String creditSwitchBaseUrl;

    @Value("${account.pool.airtime}")
    private String airtimeDataPoolAccount;
    @Value("${transaction.code.airtime}")
    private String airtimeTransactionCode;
    @Value("${transaction.code.bills}")
    private String billsTransactionCode;

    @Autowired
    private CreditSwitchGenericService creditSwitchGenericService;

    @Value("${digital.branch}")
    private String digitalBranch;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FundsTransferService fundsTransferService;

    @Autowired
    AesService aesService;

    @Autowired
    Gson gson;


    @Override
    public String processAirtimeRequest(String token, AirtimeRequestPayload requestPayload) {

        OmniResponsePayload omniResponsePayload = new OmniResponsePayload();

        // Build the request body to be sent to CreditSwitch
        AirtimeRequestDTO requestDTO = new AirtimeRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setKey(publicKey);
        requestDTO.setRequestId(requestPayload.getRequestId());
        requestDTO.setServiceId(this.getAirtimeServiceCodeFromTelco(requestPayload.getTelco()));
        requestDTO.setAmount(requestPayload.getAmount());
        requestDTO.setRecipient(requestPayload.getMobileNumber());
        requestDTO.setPrivateKey(privateKey);
        requestDTO.setDate(LocalDateTime.now().toString());
        String checksum = creditSwitchGenericService.computeCheckSum(requestDTO);
        requestDTO.setChecksum(checksum);
        requestDTO.setPrivateKey(null);

        FundsTransferPayload fundsTransferPayload = new FundsTransferPayload();
        
        fundsTransferPayload.setBeneficiaryMobileNumber(requestDTO.getRecipient());
        fundsTransferPayload.setGateway("CreditSwitch");
        fundsTransferPayload.setCategory("BILL/AIRTIME");
        try {
            fundsTransferPayload.setNarration(requestPayload.getTelco().concat("/").concat("AIRTIME TOP-UP FOR ").concat(requestDTO.getRecipient()));
        } catch (Exception e) {
            fundsTransferPayload.setNarration("Airtime Purchase");
        }
        
        fundsTransferPayload.setMobileNumber(requestPayload.getMobileNumber());
        fundsTransferPayload.setBranchCode(digitalBranch);
        fundsTransferPayload.setPin(requestPayload.getPin());
        fundsTransferPayload.setOriginatorName(requestPayload.getOriginatorName());
        fundsTransferPayload.setDebitAccount(requestPayload.getDebitAccount());
        fundsTransferPayload.setCreditAccount(airtimeDataPoolAccount);
        fundsTransferPayload.setDeviceId(requestPayload.getDeviceId());
        fundsTransferPayload.setAmount(requestPayload.getAmount());
        fundsTransferPayload.setRequestId(requestPayload.getRequestId());
        fundsTransferPayload.setBeneficiaryName("VAS POOL ACCOUNT");
        fundsTransferPayload.setCurrencyCode(requestPayload.getCurrencyCode() == null ? "NGN" : requestPayload.getCurrencyCode());
        fundsTransferPayload.setTransactionType(airtimeTransactionCode);
        fundsTransferPayload.setChannelName(requestPayload.getChannelName());
        String fundsTransferRequestJson = gson.toJson(fundsTransferPayload);

        String fundsTransferResponseJsonPost = fundsTransferService.localTransfer(token, fundsTransferRequestJson);

        // Check the status of funds-transfer to provide value to customer.
        FundsTransferResponsePayload fundsTransferResponsePayload = gson.fromJson(fundsTransferResponseJsonPost, FundsTransferResponsePayload.class);
        String responseCode = fundsTransferResponsePayload.getResponseCode();
        if (responseCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
            // Call CreditSwitch to provide airtime value.
            String url = creditSwitchBaseUrl.concat(ApiPath.CS_BUY_AIRTIME);
            CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
            if (response.isError()) {
                return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
            }

            // Build the response payload to the client.
            AirtimeDataResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), AirtimeDataResponseDTO.class);
            AirtimeDataResponsePayload responsePayload = new AirtimeDataResponsePayload();
            responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
            responsePayload.setResponseMessage(responseDTO.getStatusDescription());
            responsePayload.setTransactionRef(responseDTO.getTranxReference().toString());
            responsePayload.setUserAcct(requestPayload.getDebitAccount());
            // apply encryption here
            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
            return gson.toJson(oGenericPayload);
        }

        // Here the funds-transfer debit failed. Report the failure to client.
        omniResponsePayload.setResponseCode(fundsTransferResponsePayload.getResponseCode());
        omniResponsePayload.setResponseMessage(fundsTransferResponsePayload.getResponseMessage());
        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniResponsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processDataRequest(String token, DataRequestPayload requestPayload) {

        OmniResponsePayload omniResponsePayload = new OmniResponsePayload();

        // Build the request body to be sent to CreditSwitch
        DataRequestDTO requestDTO = new DataRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setKey(publicKey);
        requestDTO.setRequestId(requestPayload.getRequestId());
        requestDTO.setServiceId(this.getAirtimeServiceCodeFromTelco(requestPayload.getTelco()));
        requestDTO.setProductId(requestPayload.getDataPlanId());
        requestDTO.setRecipient(requestPayload.getBeneficiaryMobileNumber() == null ? requestPayload.getMobileNumber() : requestPayload.getBeneficiaryMobileNumber());
        requestDTO.setPrivateKey(privateKey);
        requestDTO.setDate(LocalDateTime.now().toString());
        requestDTO.setPrivateKey(privateKey);

        // Get the amount of the data bundle.
        DataBundlePayload oDataBundlePayload = new DataBundlePayload();
        oDataBundlePayload.setAppUser(requestPayload.getAppUser());
        oDataBundlePayload.setProductId(requestPayload.getDataPlanId());
        oDataBundlePayload.setTelco(requestPayload.getTelco());
        
        String amount = this.getAmountOfDataBundleByProductId(oDataBundlePayload);
        if (amount == null) {
            String dataPlanRetrievalFailure = messageSource.getMessage("appMessages.thirdPartyRequest.dataPlans.unavailable", null, Locale.ENGLISH);
            omniResponsePayload.setResponseCode(ResponseCode.SERVICE_UNAVAILABLE.getResponseCode());
            omniResponsePayload.setResponseMessage(dataPlanRetrievalFailure);
            return gson.toJson(omniResponsePayload);
        }
        if (amount.isBlank() || amount.isEmpty()) {
            String failedPlanIdMessage = messageSource.getMessage("appMessages.clientRequest.dataPlan.invalidDataPlanId", null, Locale.ENGLISH);
            omniResponsePayload.setResponseCode(ResponseCode.FAILED_MODEL.getResponseCode());
            omniResponsePayload.setResponseMessage(failedPlanIdMessage);
            return gson.toJson(omniResponsePayload);
        }

        requestDTO.setAmount(amount);
        String checksum = creditSwitchGenericService.computeCheckSum(requestDTO);
        requestDTO.setChecksum(checksum);
        requestDTO.setPrivateKey(null);

        FundsTransferPayload fundsTransferPayload = new FundsTransferPayload();
        
        fundsTransferPayload.setBeneficiaryMobileNumber(requestDTO.getRecipient());
        fundsTransferPayload.setGateway("CreditSwitch");
        fundsTransferPayload.setCategory("BILL/DATA");
        try {
            fundsTransferPayload.setNarration(requestPayload.getTelco().concat("/").concat("DATA PURCHASE FOR ").concat(requestDTO.getRecipient()));
        } catch (Exception e) {
            fundsTransferPayload.setNarration("Data Purchase");
        }

        fundsTransferPayload.setMobileNumber(requestPayload.getMobileNumber());
        fundsTransferPayload.setBranchCode(digitalBranch);
        fundsTransferPayload.setPin(requestPayload.getPin());
        fundsTransferPayload.setOriginatorName(requestPayload.getOriginatorName());
        fundsTransferPayload.setDebitAccount(requestPayload.getDebitAccount());
        fundsTransferPayload.setCreditAccount(airtimeDataPoolAccount);
        fundsTransferPayload.setDeviceId(requestPayload.getDeviceId());
        
        fundsTransferPayload.setAmount(amount);
        fundsTransferPayload.setRequestId(requestPayload.getRequestId());
        fundsTransferPayload.setBeneficiaryName("VAS POOL ACCOUNT");
        fundsTransferPayload.setCurrencyCode(requestPayload.getCurrencyCode() == null ? "NGN" : requestPayload.getCurrencyCode());
        fundsTransferPayload.setTransactionType(billsTransactionCode);
        fundsTransferPayload.setChannelName(requestPayload.getChannelName());
        
        String fundsTransferRequestJson = gson.toJson(fundsTransferPayload);
        String fundsTransferResponseJsonPost = fundsTransferService.localTransfer(token, fundsTransferRequestJson);

        // Check the status of funds-transfer to provide value to customer.
        FundsTransferResponsePayload fundsTransferResponsePayload = gson.fromJson(fundsTransferResponseJsonPost, FundsTransferResponsePayload.class);
        String responseCode = fundsTransferResponsePayload.getResponseCode();
        if (responseCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
            // Call CreditSwitch to provide airtime value.
            String url = creditSwitchBaseUrl.concat(ApiPath.CS_BUY_DATA);
            CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
            if (response.isError()) {
                return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
            }

            // Build the response payload to the client.
            AirtimeDataResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), AirtimeDataResponseDTO.class);
            AirtimeDataResponsePayload responsePayload = new AirtimeDataResponsePayload();
            responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
            responsePayload.setResponseMessage(responseDTO.getStatusDescription());
            responsePayload.setTransactionRef(responseDTO.getTranxReference().toString());
            responsePayload.setUserAcct(requestPayload.getDebitAccount());
            // apply encryption here
            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
            return gson.toJson(oGenericPayload);
        }

        // Here the funds-transfer debit failed. Report the failure to client.
        omniResponsePayload.setResponseCode(fundsTransferResponsePayload.getResponseCode());
        omniResponsePayload.setResponseMessage(fundsTransferResponsePayload.getResponseMessage());
        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniResponsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
//    public String processDataPlans(DataPlanRequestPayload oDataPlanRequestPayload) {
    public String processDataPlans(DataBundlePayload ooDataBundlePayload) {
        DataPlanRequestDTO dataPlanRequestDTO = new DataPlanRequestDTO();
        dataPlanRequestDTO.setServiceId(this.getDataServiceCodeFromTelco(ooDataBundlePayload.getTelco()));
        dataPlanRequestDTO.setKey(publicKey);
        dataPlanRequestDTO.setLoginId(Long.valueOf(loginId));

        String url = creditSwitchBaseUrl.concat(ApiPath.CS_DATA_PLANS);
        CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, dataPlanRequestDTO, null, null);
        if (response.isError()) {
            return creditSwitchGenericService.buildResponse(response,ooDataBundlePayload.getAppUser());
        }

        // Build response back to the client
        DataPlanResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), DataPlanResponseDTO.class);
        DataPlanResponsePayload responsePayload = new DataPlanResponsePayload();
        responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        List<DataPlan> dataPlans = responseDTO.getDataPlan().stream()
                .map(plan -> {
                    DataPlan dataPlan = new DataPlan();
                    dataPlan.setId(plan.getProductId());
                    dataPlan.setTelco(ooDataBundlePayload.getTelco());
                    dataPlan.setPlanDescription(plan.getDataBundle());
                    dataPlan.setAmount(plan.getAmount());
                    dataPlan.setDataPeriod(plan.getValidity());
                    dataPlan.setStatus(ModelStatus.OK.name());
                    dataPlan.setItemName("");
                    dataPlan.setVendor(BillPaymentVendor.CREDIT_SWITCH.getCustomName());
                    return dataPlan;
                })
                .collect(Collectors.toList());

        responsePayload.setDataPlans(dataPlans);
        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), ooDataBundlePayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

//    private String getAmountOfDataBundleByProductId(String productId, String telco) {
//        // Try to fetch from the memory storage and then do network call if it doesn't exist in memory.
//        List<DataPlan> dataPlans;
//        String dataPlanResponse = this.processDataPlans(oDataBundlePayload);
//
//        GenericPayload oGenericPayload = gson.fromJson(dataPlanResponse, GenericPayload.class);
//        DataPlanResponsePayload responsePayload = gson.fromJson(aesService.decrypt(oGenericPayload.getResponse(), aesEncryptionKey), DataPlanResponsePayload.class);
//        if (responsePayload.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
//            dataPlans = responsePayload.getDataPlans();
//        } else {
//            return null;
//        }
//
//        DataPlan dataPlan = dataPlans.stream()
//                .filter(plan -> plan.getId().equalsIgnoreCase(productId))
//                .findFirst().orElse(null);
//        if (dataPlan != null) {
//            return dataPlan.getAmount();
//        }
//        return Strings.EMPTY;
//    }
    
    private String getAmountOfDataBundleByProductId(DataBundlePayload oDataBundlePayload) {
        // Try to fetch from the memory storage and then do network call if it doesn't exist in memory.
        List<DataPlan> dataPlans;
        String dataPlanResponse = this.processDataPlans(oDataBundlePayload);

        GenericPayload oGenericPayload = gson.fromJson(dataPlanResponse, GenericPayload.class);
        DataPlanResponsePayload responsePayload = gson.fromJson(aesService.decrypt(oGenericPayload.getResponse(), oDataBundlePayload.getAppUser()), DataPlanResponsePayload.class);
        if (responsePayload.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
            dataPlans = responsePayload.getDataPlans();
        } else {
            return null;
        }

        DataPlan dataPlan = dataPlans.stream()
                .filter(plan -> plan.getId().equalsIgnoreCase(oDataBundlePayload.getProductId()))
                .findFirst().orElse(null);
        if (dataPlan != null) {
            return dataPlan.getAmount();
        }
        return Strings.EMPTY;
    }

    private String getAirtimeServiceCodeFromTelco(@NotNull String telco) {
        telco = telco.toUpperCase().trim();
        if (telco.contains(AirtimeDataTelco.AIRTEL.name())) {
            return CreditSwitchAirtimeTelco.AIRTEL.getServiceId();
        } else if (telco.contains(AirtimeDataTelco.ETISALAT.name())) {
            return CreditSwitchAirtimeTelco.ETISALAT.getServiceId();
        } else if (telco.contains(AirtimeDataTelco.GLO.name())) {
            return CreditSwitchAirtimeTelco.GLOBACOM.getServiceId();
        } else if (telco.contains(AirtimeDataTelco.MTN.name())) {
            return CreditSwitchAirtimeTelco.MTN.getServiceId();
        }
        return Strings.EMPTY;
    }

    private String getDataServiceCodeFromTelco(@NotNull String telco) {
        telco = telco.toUpperCase().trim();
        if (telco.contains(AirtimeDataTelco.AIRTEL.name())) {
            return CreditSwitchDataTelco.AIRTEL.getServiceCode();
        } else if (telco.contains(AirtimeDataTelco.ETISALAT.name())) {
            return CreditSwitchDataTelco.ETISALAT.getServiceCode();
        } else if (telco.contains(AirtimeDataTelco.GLO.name())) {
            return CreditSwitchDataTelco.GLOBACOM.getServiceCode();
        } else if (telco.contains(AirtimeDataTelco.MTN.name())) {
            return CreditSwitchDataTelco.MTN.getServiceCode();
        } else if (telco.contains(AirtimeDataTelco.SMILE.name())) {
            return CreditSwitchDataTelco.SMILE.getServiceCode();
        } else if (telco.contains(AirtimeDataTelco.NTEL.name())) {
            return CreditSwitchDataTelco.NTEL.getServiceCode();
        } else {
            return Strings.EMPTY;
        }
    }

//    @Override
//    public String processDataPlans(DataPlanRequestPayload oDataPlanRequestPayload) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
}
