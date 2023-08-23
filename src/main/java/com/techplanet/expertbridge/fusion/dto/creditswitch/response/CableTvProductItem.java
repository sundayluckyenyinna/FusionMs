package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

import java.util.List;

@Data
public class CableTvProductItem
{
    private List<CableTvProductPricingOption> availablePricingOptions;
    private String code;
    private String name;
    private String description;
}
