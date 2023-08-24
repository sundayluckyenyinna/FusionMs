package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import lombok.Data;

@Data
public class BettingAccountValidationRequestDTO
{
    private String loginId;
    private String key;
    private String customerId;
    private String provider;
}
