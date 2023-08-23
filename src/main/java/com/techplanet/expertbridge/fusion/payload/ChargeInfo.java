package com.techplanet.expertbridge.fusion.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeInfo
{
    @NotNull(message = "Charge type is required")
    @Pattern(regexp = "[A-Za-z]{1,}", message = "Charge type must be alphabets")
    private String chargeType;

    @NotNull(message = "Charge amount is required")
    @Pattern(regexp = "(?=.*?\\d)^\\$?(([1-9]\\d{0,2}(,\\d{3})*)|\\d+)?(\\.\\d{1,3})?$", message = "Charge amount must be numeric")
    private String chargeAmount;
}
