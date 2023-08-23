package com.techplanet.expertbridge.fusion.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CableTvValidationResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private String cardholderName;
    private String otherCustomerInfo;
    private String invoicePeriod;
    private String dueDate;
}
