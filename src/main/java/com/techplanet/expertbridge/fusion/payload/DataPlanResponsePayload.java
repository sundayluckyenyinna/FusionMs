package com.techplanet.expertbridge.fusion.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPlanResponsePayload
{
    private String responseCode;
    private List<DataPlan> dataPlans;
}
