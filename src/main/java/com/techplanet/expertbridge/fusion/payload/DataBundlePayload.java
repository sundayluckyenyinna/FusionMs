package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
public class DataBundlePayload {
    private String productId;
    private AppUser appUser;
     @NotNull(message = "telco cannot be null")
    @NotEmpty(message = "telco cannot empty")
    @NotBlank(message = "telco cannot be blank")
    private String telco;

    @NotNull(message = "requestId cannot be null")
    @NotEmpty(message = "requestId cannot empty")
    @NotBlank(message = "requestId cannot be blank")
    private String requestId;
    
}
