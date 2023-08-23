package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import lombok.Data;

@Data
public class ValidateElectricityRequestDTO
{
    @CheckSumInclude(order = 1)
    private String loginId;

    @CheckSumInclude(order = 2)
    private String serviceId;

    @CheckSumInclude(order = 4)
    private String customerAccountId;

    private String key;

    @CheckSumInclude(order = 3)
    private String privateKey;

    private String checksum;
}
