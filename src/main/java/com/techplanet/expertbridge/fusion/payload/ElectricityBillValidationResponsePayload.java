package com.techplanet.expertbridge.fusion.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectricityBillValidationResponsePayload
{
    private String responseCode;
    private String cardHolderName;
    private String otherCustomerInfo;
}
