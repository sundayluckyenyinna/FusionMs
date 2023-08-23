package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ElectricityBillPaymentRequestPayload
{
    @NotNull(message = "mobileNumber cannot be null")
    @NotEmpty(message = "mobileNumber cannot empty")
    @NotBlank(message = "mobileNumber cannot be blank")
    private String mobileNumber;

    @NotNull(message = "debitAccount cannot be null")
    @NotEmpty(message = "debitAccount cannot empty")
    @NotBlank(message = "debitAccount cannot be blank")
    private String debitAccount;

    @NotNull(message = "meterNumber cannot be null")
    @NotEmpty(message = "meterNumber cannot empty")
    @NotBlank(message = "meterNumber cannot be blank")
    private String meterNumber;

    @NotNull(message = "billerId cannot be null")
    @NotEmpty(message = "billerId cannot empty")
    @NotBlank(message = "billerId cannot be blank")
    private String billerId;

    @NotNull(message = "amount cannot be null")
    @NotEmpty(message = "amount cannot empty")
    @NotBlank(message = "amount cannot be blank")
    private String amount;

    @NotNull(message = "address cannot be null")
    @NotEmpty(message = "address cannot empty")
    @NotBlank(message = "address cannot be blank")
    private String address;

    @NotNull(message = "customerName cannot be null")
    @NotEmpty(message = "customerName cannot empty")
    @NotBlank(message = "customerName cannot be blank")
    private String customerName;

    @NotNull(message = "token cannot be null")
    @NotEmpty(message = "token cannot empty")
    @NotBlank(message = "token cannot be blank")
    private String token;

    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;

    @NotNull(message = "pin cannot be null")
    @NotEmpty(message = "pin cannot empty")
    @NotBlank(message = "pin cannot be blank")
    private String pin;

    @NotNull(message = "deviceId cannot be null")
    @NotEmpty(message = "deviceId cannot empty")
    @NotBlank(message = "deviceId cannot be blank")
    private String deviceId;

    @NotNull(message = "channelName cannot be null")
    @NotEmpty(message = "channelName cannot empty")
    @NotBlank(message = "channelName cannot be blank")
    private String channelName;

    private String currencyCode;
    private AppUser appUser;
}
