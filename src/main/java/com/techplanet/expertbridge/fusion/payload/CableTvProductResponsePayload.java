package com.techplanet.expertbridge.fusion.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CableTvProductResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private List<CableTvProductResponseData> billers;
}
