package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BetAccountFundingRequestPayload
{
    @NotNull(message = "mobileNumber cannot be null")
    @NotBlank(message = "mobileNumber cannot be blank")
    @NotEmpty(message = "mobileNumber cannot be empty")
    private String mobileNumber;

    @NotNull(message = "debitAccount cannot be null")
    @NotBlank(message = "debitAccount cannot be blank")
    @NotEmpty(message = "debitAccount cannot be empty")
    private String debitAccount;

    @NotNull(message = "deviceId cannot be null")
    @NotBlank(message = "deviceId cannot be blank")
    @NotEmpty(message = "deviceId cannot be empty")
    private String deviceId;

    private String currencyCode = "NGN";

    @NotNull(message = "channelName cannot be null")
    @NotBlank(message = "channelName cannot be blank")
    @NotEmpty(message = "channelName cannot be empty")
    private String channelName;

    @NotNull(message = "pin cannot be null")
    @NotBlank(message = "pin cannot be blank")
    @NotEmpty(message = "pin cannot be empty")
    private String pin;

    @NotNull(message = "serviceId cannot be null")
    @NotBlank(message = "serviceId cannot be blank")
    @NotEmpty(message = "serviceId cannot be empty")
    private String serviceId;

    @NotNull(message = "customerId cannot be null")
    @NotBlank(message = "customerId cannot be blank")
    @NotEmpty(message = "customerId cannot be empty")
    private String customerId;

    @NotNull(message = "providerName cannot be null")
    @NotBlank(message = "providerName cannot be blank")
    @NotEmpty(message = "providerName cannot be empty")
    private String providerName;

    @NotNull(message = "customerName cannot be null")
    @NotBlank(message = "customerName cannot be blank")
    @NotEmpty(message = "customerName cannot be empty")
    private String customerName;

    @NotNull(message = "amount cannot be null")
    @NotBlank(message = "amount cannot be blank")
    @NotEmpty(message = "amount cannot be empty")
    private String amount;

    private String token;
    private AppUser appUser;
}
