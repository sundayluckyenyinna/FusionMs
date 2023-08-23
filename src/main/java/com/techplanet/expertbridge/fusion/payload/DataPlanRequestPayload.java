package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DataPlanRequestPayload
{
    @NotNull(message = "telco cannot be null")
    @NotEmpty(message = "telco cannot empty")
    @NotBlank(message = "telco cannot be blank")
    private String telco;

    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;
    
    private AppUser appUser;
}
