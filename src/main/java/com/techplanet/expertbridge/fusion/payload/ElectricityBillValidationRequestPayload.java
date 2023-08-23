package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ElectricityBillValidationRequestPayload
{
    @NotNull(message = "meterNumber cannot be null")
    @NotEmpty(message = "meterNumber cannot empty")
    @NotBlank(message = "meterNumber cannot be blank")
    private String meterNumber;

    @NotNull(message = "biller cannot be null")
    @NotEmpty(message = "biller cannot empty")
    @NotBlank(message = "biller cannot be blank")
    private String biller;

    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;
    
    private AppUser appUser;
}
