package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Daniel Ofoleta
 */
@Data
public class BvnRequestPayload {
    @NotNull(message = "bvn cannot be null")
    @NotEmpty(message = "bvn cannot empty")
    @NotBlank(message = "bvn cannot be blank")
    private String bvn;

    @NotNull(message = "lastName cannot be null")
    @NotEmpty(message = "lastName cannot empty")
    @NotBlank(message = "lastName cannot be blank")
    private String lastName;
    
    private AppUser appUser;
}
