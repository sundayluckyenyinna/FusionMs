package com.techplanet.expertbridge.fusion.payload;

import lombok.Data;

@Data
public class DataPlan
{
    private String id;
    private String telco;
    private String planDescription;
    private String amount;
    private String dataPeriod;
    private String status;
    private String itemName;
    private String vendor;
}
