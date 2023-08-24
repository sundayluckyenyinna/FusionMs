package com.techplanet.expertbridge.fusion.payload;

import lombok.Data;

@Data
public class BettingProviderData
{
    private String providerName;
    private String serviceId;
    private String idempotency;
}
