package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import lombok.Data;

@Data
public class AirtimeRequestDTO
{
    @CheckSumInclude(order = 1)
    private String loginId;

    private String key;

    @CheckSumInclude(order = 2)
    private String requestId;

    @CheckSumInclude(order = 3)
    private String serviceId;

    @CheckSumInclude(order = 4)
    private String amount;

    @CheckSumInclude(order = 6)
    private String recipient;

    @CheckSumInclude(order = 5)
    private String privateKey;

    private String date;
    private String checksum;
}
