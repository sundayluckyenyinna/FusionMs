package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataRequestDTO
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

    @CheckSumInclude(order = 5)
    private String privateKey;

    @CheckSumInclude(order = 6)
    private String recipient;

    private String date;

    private String productId;

    private String checksum;
}
