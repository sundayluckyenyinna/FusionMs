package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BetAccountValidationRequestPayload
{
    @NotNull(message = "providerName cannot be null")
    @NotEmpty(message = "providerName cannot be empty")
    @NotBlank(message = "providerName cannot be blank")
    private String providerName;

    @NotNull(message = "customerId cannot be null")
    @NotEmpty(message = "customerId cannot be empty")
    @NotBlank(message = "customerId cannot be blank")
    private String customerId;

    private String token;
    private AppUser appUser;
}
