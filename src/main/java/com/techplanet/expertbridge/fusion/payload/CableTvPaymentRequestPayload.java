package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CableTvPaymentRequestPayload
{
    @NotNull(message = "mobileNumber cannot be null")
    @NotEmpty(message = "mobileNumber cannot empty")
    @NotBlank(message = "mobileNumber cannot be blank")
    private String mobileNumber;

    @NotNull(message = "debitAccount cannot be null")
    @NotEmpty(message = "debitAccount cannot empty")
    @NotBlank(message = "debitAccount cannot be blank")
    private String debitAccount;

    @NotNull(message = "smartCard cannot be null")
    @NotEmpty(message = "smartCard cannot empty")
    @NotBlank(message = "smartCard cannot be blank")
    private String smartCard;

    @NotNull(message = "billerId cannot be null")
    @NotEmpty(message = "billerId cannot empty")
    @NotBlank(message = "billerId cannot be blank")
    private String billerId;

    @NotNull(message = "amount cannot be null")
    @NotEmpty(message = "amount cannot empty")
    @NotBlank(message = "amount cannot be blank")
    private String amount;

    @NotNull(message = "customerName cannot be null")
    @NotEmpty(message = "customerName cannot empty")
    @NotBlank(message = "customerName cannot be blank")
    private String customerName;

    @NotNull(message = "productId cannot be null")
    @NotEmpty(message = "productId cannot empty")
    @NotBlank(message = "productId cannot be blank")
    private List<String> productId;

    @NotNull(message = "invoicePeriod cannot be null")
    @NotEmpty(message = "invoicePeriod cannot empty")
    @NotBlank(message = "invoicePeriod cannot be blank")
    private String invoicePeriod;

    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;

    private String token;

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
