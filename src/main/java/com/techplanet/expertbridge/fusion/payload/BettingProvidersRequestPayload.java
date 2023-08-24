package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BettingProvidersRequestPayload
{
    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot be empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;

    private String token;
    private AppUser appUser;
}
