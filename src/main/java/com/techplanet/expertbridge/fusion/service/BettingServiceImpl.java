package com.techplanet.expertbridge.fusion.service;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.ApiPath;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.BetAccountFundingRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.BetAccountFundingResponseDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.request.BettingAccountValidationRequestDTO;
import com.techplanet.expertbridge.fusion.dto.creditswitch.response.*;
import com.techplanet.expertbridge.fusion.jwt.JwtTokenUtil;
import com.techplanet.expertbridge.fusion.payload.*;
import com.techplanet.expertbridge.fusion.repository.CustomerRepository;
import com.techplanet.expertbridge.fusion.security.AesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class BettingServiceImpl implements BettingService
{

    @Value("${thirdParty.creditSwitch.loginId}")
    private String loginId;

    @Value("${thirdParty.creditSwitch.apiKey}")
    private String publicKey;

    @Value("${thirdParty.creditSwitch.privateKey}")
    private String privateKey;

    @Value("${thirdParty.creditSwitch.baseUrl}")
    private String creditSwitchBaseUrl;

    @Value("${account.pool.betting}")
    private String bettingPoolAccount;

    @Autowired
    private CreditSwitchGenericService creditSwitchGenericService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${digital.branch}")
    private String digitalBranch;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FundsTransferService fundsTransferService;

    @Value("${transaction.code.bills}")
    private String billsTransactionCode;

    @Autowired
    AesService aesService;

    @Autowired
    private Gson gson;


    @Override
    public String processBettingProvidersListRequest(BettingProvidersRequestPayload requestPayload){
        BettingProviderResponsePayload responsePayload = new BettingProviderResponsePayload();
        String csApiPath = String.format(ApiPath.CS_BETTING_PROVIDERS, loginId, publicKey);
        String url = creditSwitchBaseUrl.concat(csApiPath);
        CreditSwitchResponse response = creditSwitchGenericService.getForObject(url, null, null);
        if(response.isError()){
            return creditSwitchGenericService.buildResponse(response, requestPayload.getAppUser());
        }

        BettingProvidersResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), BettingProvidersResponseDTO.class);
        HashMap<String, String> resultMap = responseDTO.getResult();

        List<BettingProviderData> responseData = new ArrayList<>();
        for(Map.Entry<String, String> entry : resultMap.entrySet()){
            BettingProviderData data = new BettingProviderData();
            data.setProviderName(entry.getValue());
            data.setServiceId(entry.getKey());
            data.setIdempotency(UUID.randomUUID().toString());
            responseData.add(data);
        }

        responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        responsePayload.setResponseMessage("Successful operation");
        responsePayload.setResponseData(responseData);

        // apply encryption
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processBetAccountValidation(BetAccountValidationRequestPayload requestPayload){
        BetAccountValidationResponsePayload responsePayload = new BetAccountValidationResponsePayload();

        BettingAccountValidationRequestDTO requestDTO = new BettingAccountValidationRequestDTO();
        requestDTO.setLoginId(loginId);
        requestDTO.setKey(publicKey);
        requestDTO.setProvider(requestPayload.getProviderName());
        requestDTO.setCustomerId(requestPayload.getCustomerId());

        String url = creditSwitchBaseUrl.concat(ApiPath.CS_BET_ACCOUNT_VALIDATION);
        CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
        if(response.isError()){
            return creditSwitchGenericService.buildResponse(response, requestPayload.getAppUser());
        }

        BetAccountValidationResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), BetAccountValidationResponseDTO.class);
        if(responseDTO.getStatusCode().equals(ResponseCode.SUCCESS_CODE.getResponseCode())){
            responsePayload.setResponseCode(responseDTO.getStatusCode());
            responsePayload.setResponseMessage(responseDTO.getStatusDescription());
            responsePayload.setStatus(responseDTO.isStatus());

            BetAccountValidationResponseData responseData = gson.fromJson(gson.toJson(responseDTO.getResult()), BetAccountValidationResponseData.class);
            responsePayload.setResponseData(responseData);

            // apply encryption
            GenericPayload oGenericPayload = new GenericPayload();
            oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
            return gson.toJson(oGenericPayload);
        }

        responsePayload.setResponseCode(responseDTO.getStatusCode());
        responsePayload.setResponseMessage(responseDTO.getStatusDescription());

        // apply encryption
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    @Override
    public String processBetAccountFunding(BetAccountFundingRequestPayload requestPayload){
        BetAccountFundingResponsePayload responsePayload = new BetAccountFundingResponsePayload();

        BetAccountFundingRequestDTO requestDTO = new BetAccountFundingRequestDTO();
        requestDTO.setRequestId(UUID.randomUUID().toString());
        requestDTO.setAmount(requestPayload.getAmount());
        requestDTO.setKey(publicKey);
        requestDTO.setServiceId(requestPayload.getServiceId());
        requestDTO.setCustomerName(requestPayload.getCustomerName());
        requestDTO.setCustomerId(requestPayload.getCustomerId());
        requestDTO.setLoginId(loginId);
        requestDTO.setProviderName(requestPayload.getProviderName());

        // First debit the customer locally.
        FundsTransferPayload fundsTransferPayload = new FundsTransferPayload();

        fundsTransferPayload.setBeneficiaryMobileNumber(requestPayload.getMobileNumber());
        fundsTransferPayload.setGateway("CreditSwitch");
        fundsTransferPayload.setCategory("BILL/BETTING");
        fundsTransferPayload.setNarration("Bet account funding");

        fundsTransferPayload.setMobileNumber(requestPayload.getMobileNumber());
        fundsTransferPayload.setBranchCode(digitalBranch);
        fundsTransferPayload.setPin(requestPayload.getPin());
        fundsTransferPayload.setOriginatorName(requestPayload.getCustomerName());
        fundsTransferPayload.setDebitAccount(requestPayload.getDebitAccount());
        fundsTransferPayload.setCreditAccount(bettingPoolAccount);
        fundsTransferPayload.setDeviceId(requestPayload.getDeviceId());

        fundsTransferPayload.setAmount(requestPayload.getAmount());
        fundsTransferPayload.setRequestId(requestDTO.getRequestId());
        fundsTransferPayload.setBeneficiaryName("VAS POOL ACCOUNT");
        fundsTransferPayload.setCurrencyCode(requestPayload.getCurrencyCode() == null ? "NGN" : requestPayload.getCurrencyCode());
        fundsTransferPayload.setTransactionType(billsTransactionCode);
        fundsTransferPayload.setChannelName(requestPayload.getChannelName());

        String fundsTransferRequestJson = gson.toJson(fundsTransferPayload);
        String fundsTransferResponseJsonPost = fundsTransferService.localTransfer(requestPayload.getToken(), fundsTransferRequestJson);

        // Check the status of funds-transfer to provide value to customer.
        FundsTransferResponsePayload fundsTransferResponsePayload = gson.fromJson(fundsTransferResponseJsonPost, FundsTransferResponsePayload.class);
        String responseCode = fundsTransferResponsePayload.getResponseCode();
        if(responseCode.equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {

            // Call Credit switch to provide value to customer.
            String url = creditSwitchBaseUrl.concat(ApiPath.CS_BETTING_PAY);
            CreditSwitchResponse response = creditSwitchGenericService.postForObject(url, requestDTO, null, null);
            if(response.isError()){
                return creditSwitchGenericService.buildResponse(response, requestPayload.getAppUser());
            }
            BetAccountFundingResponseDTO responseDTO = gson.fromJson(response.getSuccessResponse(), BetAccountFundingResponseDTO.class);
            if (responseDTO.getStatusCode().equalsIgnoreCase(ResponseCode.SUCCESS_CODE.getResponseCode())) {
                responsePayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
                responsePayload.setResponseMessage(responseDTO.getStatusDescription());
                BetAccountFundingResponseData responseData = responseDTO.getResult();
                responseData.setInternalRef(requestDTO.getRequestId());
                responsePayload.setResponseData(responseData);

                // apply encryption
                GenericPayload oGenericPayload = new GenericPayload();
                oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
                return gson.toJson(oGenericPayload);
            }

            responsePayload.setResponseCode(responseDTO.getStatusCode());
            responsePayload.setResponseMessage(responseDTO.getStatusDescription());
        }else{
            responsePayload.setResponseCode(responseCode);
            responsePayload.setResponseMessage(fundsTransferResponsePayload.getResponseMessage());
        }

        // apply encryption
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(responsePayload), requestPayload.getAppUser()));
        return gson.toJson(oGenericPayload);
    }
}
