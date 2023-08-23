package com.techplanet.expertbridge.fusion.payload;

import lombok.Data;

@Data

public class CableTvProductResponseData
{
    private String id;
    private String biller;
    private String bouquet;
    private String packageName;
    private String amount;
    private String monthlyAmount;
    private int availableMonths;
    private String yearlyAmount;
    private String billerId;
    private String productId;
    private String status;
    private String vendor;
}
