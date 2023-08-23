package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DataRequestPayload
{
    @NotNull(message = "mobileNumber cannot be null")
    @NotEmpty(message = "mobileNumber cannot empty")
    @NotBlank(message = "mobileNumber cannot be blank")
    private String mobileNumber;

    @NotNull(message = "debitAccount cannot be null")
    @NotEmpty(message = "debitAccount cannot empty")
    @NotBlank(message = "debitAccount cannot be blank")
    private String debitAccount;

    @NotNull(message = "telco cannot be null")
    @NotEmpty(message = "telco cannot empty")
    @NotBlank(message = "telco cannot be blank")
    private String telco;

    @NotNull(message = "dataPlanId cannot be null")
    @NotEmpty(message = "dataPlanId cannot empty")
    @NotBlank(message = "dataPlanId cannot be blank")
    private String dataPlanId;

    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;

    @NotNull(message = "token cannot be null")
    @NotEmpty(message = "token cannot empty")
    @NotBlank(message = "token cannot be blank")
    private String token;

    @NotNull(message = "pin cannot be null")
    @NotEmpty(message = "pin cannot empty")
    @NotBlank(message = "pin cannot be blank")
    private String pin;

    @NotNull(message = "originatorName cannot be null")
    @NotEmpty(message = "originatorName cannot empty")
    @NotBlank(message = "originatorName cannot be blank")
    private String originatorName;

    @NotNull(message = "deviceId cannot be null")
    @NotEmpty(message = "deviceId cannot empty")
    @NotBlank(message = "deviceId cannot be blank")
    private String deviceId;

    @NotNull(message = "channelName cannot be null")
    @NotEmpty(message = "channelName cannot empty")
    @NotBlank(message = "channelName cannot be blank")
    private String channelName;
    private String currencyCode;
    private String beneficiaryMobileNumber;
    
    private AppUser appUser;
}
