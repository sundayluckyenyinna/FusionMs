package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import lombok.Data;

@Data
public class CableTvValidationRequestDTO
{
    @CheckSumInclude(order = 1)
    private String loginId;

    private String key;

    @CheckSumInclude(order = 2)
    private String privateKey;

    private String checksum;

    @CheckSumInclude(order = 3)
    private String smartCardCode;

    @CheckSumInclude(order = 3)
    private String customerNo;

    private String serviceId;
}
