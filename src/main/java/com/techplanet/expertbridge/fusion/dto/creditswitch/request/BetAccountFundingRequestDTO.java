package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import lombok.Data;

@Data
public class BetAccountFundingRequestDTO
{
    private String loginId;
    private String key;
    private String serviceId;
    private String customerId;
    private String providerName;
    private String customerName;
    private String amount;
    private String requestId;
}
