package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

import java.util.List;

@Data
public class DataPlanResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private String serviceId;
    private List<CreditSwitchDataPlan> dataPlan;
}
