package com.techplanet.expertbridge.fusion.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundsTransferPayload {

    @NotNull(message = "Mobile number cannot be null")
    @NotEmpty(message = "Mobile number cannot be empty")
    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "[0-9]{11}", message = "11 digit mobile number required")
    private String mobileNumber;

    @NotNull(message = "Branch code cannot be null")
    @NotEmpty(message = "Branch code cannot be empty")
    @NotBlank(message = "Branch code cannot be blank")
    @Pattern(regexp = "^[A-Za-z]{2}[0-9]{7}$", message = "Branch code like BB0010000 required")
    private String branchCode;

    @NotBlank(message = "Debit account is required")
    @Pattern(regexp = "^[0-9]{11}|(NGN1([0-9]{4})([0-9]{1})([0-9]{3})|NGN1([0-9]{4})([0-9]{1})([0-9]{3})([0]{2})([0-9]{2})|[0-9]{10})$", message = "Debit account must be either 10 Digit Customer Account or NGN account number")
    @Schema(name = "Debit Account Number", example = "0123456789", description = "10 digit NUBAN account number")
    private String debitAccount;

    @NotBlank(message = "Credit account is required")
    @Pattern(regexp = "^(PL([5-6])([0-9]{4}))|(NGN1([0-9]{4})([0-9]{1})([0-9]{3})|NGN1([0-9]{4})([0-9]{1})([0-9]{3})([0]{2})([0-9]{2})|[0-9]{10})$", message = "Credit account must be either PL or NGN account number")
    @Schema(name = "Credit Account Number", example = "0123456789", description = "PL Account or Internal Account")
    private String creditAccount;

    @NotBlank(message = "Transaction narration is required")
    @Schema(name = "Transaction Narration", example = "Cash Withdrawal IFO Brian Okon", description = "Transaction Narration")
    private String narration;

    @NotBlank(message = "Transaction amount is required")
    @Pattern(regexp = "(?=.*?\\d)^\\$?(([1-9]\\d{0,2}(,\\d{3})*)|\\d+)?(\\.\\d{1,3})?$", message = "Transaction Amount must contain only digits, comma or dot only")
    @Schema(name = "Transaction Amount", example = "1,000.00", description = "Transaction Amount")
    private String amount;

    @NotBlank(message = "Request ID is required")
    @Schema(name = "Request ID", example = "PYLON67XXTY78999GHTRE", description = "Request ID is required")
    private String requestId;

    @NotNull(message = "pin cannot be null")
    @NotEmpty(message = "Pin cannot be empty")
    @NotBlank(message = "Pin cannot be blank")
    private String pin;

    @NotNull(message = "originatorName cannot be null")
    @NotEmpty(message = "originatorName cannot be empty")
    @NotBlank(message = "originatorName cannot be blank")
    private String originatorName;

    @NotNull(message = "deviceId cannot be null")
    @NotEmpty(message = "deviceId cannot be empty")
    @NotBlank(message = "deviceId cannot be blank")
    private String deviceId;

    @NotNull(message = "beneficiaryName cannot be null")
    @NotEmpty(message = "beneficiaryName cannot be empty")
    @NotBlank(message = "beneficiaryName cannot be blank")
    private String beneficiaryName;
    private String currencyCode;
    private String channelName; 
    private String transactionType;
    private String securityAnswer; 
    private String transRef;
    
    private String gateway;
    private String destinationBankCode;
    private int debitAccountTier =0;
    private String beneficiaryMobileNumber;
    private String category;
}
