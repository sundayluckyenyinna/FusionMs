package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.*;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.ElectricityBillPaymentRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.ValidateElectricityRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.CreditSwitchResponse;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.ElectricityBillPaymentResponseDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.ElectricityValidationBillData;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.ValidateElectricityResponseDTO;
import com.techplanet.expertbridge.fusion.model.BillerLookupData;
import com.techplanet.expertbridge.fusion.payload.*;
import com.techplanet.expertbridge.fusion.repository.CableTvRepository;
import com.techplanet.expertbridge.fusion.security.AesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ElectricityServiceImpl implements ElectricityService {

    @Autowired
    private Environment env;
    @Value("${thirdParty.creditSwitch.loginId}")
    private String loginId;

    @Value("${thirdParty.creditSwitch.apiKey}")
    private String publicKey;

    @Value("${thirdParty.creditSwitch.privateKey}")
    private String privateKey;

    @Value("${thirdParty.creditSwitch.baseUrl}")
    private String creditSwitchBaseUrl;

    @Value("${expertBridge.branch.defaultBranch}")
    private String defaultBranch;

    @Value("${expertBridge.accounts.electricityPoolAccount}")
    private String electricityPoolAccount;

    @Value("${transaction.code.bills}")
    private String billsTransactionCode;

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

//    @Value("${aes.encryption.key}")
//    private String aesEncryptionKey;

    @Autowired
    private CableTvRepository cableTvRepository;

    @Override
    public String processValidateElectricityRequest(ElectricityBillValidationRequestPayload requestPayload) {

        // Build the request to Credit Switch
        ValidateElectricityRequestDTO requestDTO = new ValidateElectricityRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setServiceId(requestPayload.getBiller());
        requestDTO.setCustomerAccountId(requestPayload.getMeterNumber());
        requestDTO.setKey(publicKey);
        requestDTO.setPrivateKey(privateKey);
        String checksum = creditSwitchGenericService.computeCheckSum(requestDTO);
        requestDTO.setChecksum(checksum);
        requestDTO.setPrivateKey(null);

        // Call Credit Switch to validate the bill
        String url = creditSwitchBaseUrl.concat(ApiPath.CS_VALIDATE_ELECTRICITY);
        CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
        if (response.isError()) {
            return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
        }

        // Build response to the client
        ValidateElectricityResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), ValidateElectricityResponseDTO.class);
        ElectricityBillValidationResponsePayload responsePayload = new ElectricityBillValidationResponsePayload();
        responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        responsePayload.setCardHolderName(responseDTO.getDetail().getName());
        ElectricityValidationBillData detail = responseDTO.getDetail();
        String[] otherInfoArray = new String[]{detail.getMeterNumber(), detail.getAddress(), detail.getProviderRef()};
        responsePayload.setOtherCustomerInfo(String.join(StringValues.SPACE_STROKE, new ArrayList<>(List.of(otherInfoArray))));

        // apply encryption here
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processElectricityBillersRequest(ElectricityBillerRequestPayload requestPayload) {
        String providerName = env.getProperty("thirdParty.bills.vendor");
        if (providerName == null) {
            log.info("No vendor found in the configuration file");
            providerName = BillPaymentVendor.CREDIT_SWITCH.name();
        } else {
            log.info("Bills Payment provider name found as : {}", providerName.toUpperCase());
            providerName = providerName.toUpperCase();
        }

        String biller = requestPayload.getBiller() == null ? "ELECTRICITY" : requestPayload.getBiller().toUpperCase();

        AtomicInteger counter = new AtomicInteger();
        List<BillerLookupData> billerLookupData = cableTvRepository.findBillerLookupByBillerIdAndProviderFromConfig(biller, providerName);
        List<ElectricityBillerData> billerData = billerLookupData.stream()
                .map(lookupData -> {
                    ElectricityBillerData data = new ElectricityBillerData();
                    data.setId(String.valueOf(counter.incrementAndGet()));
                    data.setBiller(lookupData.getBillerCode());
                    data.setBillerId(lookupData.getBillerId());
                    data.setVendor(lookupData.getVendorName());
                    data.setStatus(ModelStatus.OK.name());
                    data.setPackageName(lookupData.getBillerName());
                    data.setBouquet(lookupData.getBillerDescription());
                    return data;
                })
                .collect(Collectors.toList());

        ElectricityBillerResponsePayload responsePayload = new ElectricityBillerResponsePayload();
        responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        responsePayload.setResponseMessage(messageSource.getMessage("appMessages.request.success", null, Locale.ENGLISH));
        responsePayload.setBiller(billerData);

        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processElectricityPayment(ElectricityBillPaymentRequestPayload requestPayload) {
        // Build the request to Credit Switch
        ElectricityBillPaymentRequestDTO requestDTO = new ElectricityBillPaymentRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setServiceId(requestPayload.getBillerId());
        requestDTO.setCustomerAccountId(requestPayload.getMeterNumber());
        requestDTO.setAmount(requestPayload.getAmount());
        requestDTO.setCustomerName(requestPayload.getCustomerName());
        requestDTO.setRequestId(requestPayload.getRequestId());
        requestDTO.setCustomerAddress(requestPayload.getAddress());
        requestDTO.setKey(publicKey);
        requestDTO.setPrivateKey(privateKey);
        String checksum = creditSwitchGenericService.computeCheckSum(requestDTO);
        requestDTO.setChecksum(checksum);
        requestDTO.setPrivateKey(null);

        // Debit the customer for the transaction.
        FundsTransferPayload fundsTransferPayload = new FundsTransferPayload();

        fundsTransferPayload.setBeneficiaryMobileNumber(requestPayload.getMeterNumber());
        fundsTransferPayload.setGateway("CreditSwitch");
        fundsTransferPayload.setCategory("BILL/ELECTRICITY");
        fundsTransferPayload.setNarration(requestPayload.getBillerId().concat("/").concat("ELECTICITY BILL FOR ").concat(requestPayload.getMeterNumber()));

        fundsTransferPayload.setMobileNumber(requestPayload.getMobileNumber());
        fundsTransferPayload.setBranchCode(defaultBranch);
        fundsTransferPayload.setPin(requestPayload.getPin());
        fundsTransferPayload.setOriginatorName(requestPayload.getCustomerName());
        fundsTransferPayload.setDebitAccount(requestPayload.getDebitAccount());
        fundsTransferPayload.setCreditAccount(electricityPoolAccount);
        fundsTransferPayload.setDeviceId(requestPayload.getDeviceId());
        fundsTransferPayload.setAmount(requestPayload.getAmount());
        fundsTransferPayload.setRequestId(requestPayload.getRequestId());
        fundsTransferPayload.setBeneficiaryName("VAS POOL ACCOUNT");
        fundsTransferPayload.setCurrencyCode(requestPayload.getCurrencyCode() == null ? "NGN" : requestPayload.getCurrencyCode());
        fundsTransferPayload.setTransactionType(billsTransactionCode);
        fundsTransferPayload.setChannelName(requestPayload.getChannelName());

        String fundsTransferRequestJson = gson.toJson(fundsTransferPayload);
        String fundsTransferResponseJsonPost = fundsTransferService.localTransfer(requestPayload.getToken(), fundsTransferRequestJson);

        // Check the status of funds-transfer to provide value to customer.
        FundsTransferResponsePayload fundsTransferResponsePayload = gson.fromJson(fundsTransferResponseJsonPost, FundsTransferResponsePayload.class);
        String responseCode = fundsTransferResponsePayload.getResponseCode();
        if (responseCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
            // Call the Credit switch
            String url = creditSwitchBaseUrl.concat(ApiPath.CS_ELECTRICITY_PAYMENT);
            CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
            if (response.isError()) {
                return creditSwitchGenericService.buildResponse(response,requestPayload.getAppUser());
            }

            // Build the response payload back to the client
            ElectricityBillPaymentResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), ElectricityBillPaymentResponseDTO.class);
            ElectricityBillPaymentResponsePayload responsePayload = new ElectricityBillPaymentResponsePayload();
            responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
            responsePayload.setResponseMessage(messageSource.getMessage("appMessages.request.success", null, Locale.ENGLISH));
            responsePayload.setCustomerName(responseDTO.getDetail().getName());
            responsePayload.setMobileNumber(requestPayload.getMobileNumber());
            responsePayload.setMeterNumber(responseDTO.getDetail().getAccountId());
            responsePayload.setRequestId(requestPayload.getRequestId());
            responsePayload.setDistributionCompany("");   // Look up the distribution company from database
            responsePayload.setTransRef(responseDTO.getDetail().getTranxId());

            // apply encryption here
            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
            return gson.toJson(oGenericPayload);
        }

        // Here the funds-transfer failed. Return message back to client.
        OmniResponsePayload responsePayload = new OmniResponsePayload();
        responsePayload.setResponseCode(fundsTransferResponsePayload.getResponseCode());
        responsePayload.setResponseMessage(fundsTransferResponsePayload.getResponseMessage());
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }
}
