package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import lombok.Data;

import java.util.List;


@Data
public class CableTvPaymentRequestDTO
{
    @CheckSumInclude(order = 1)
    private String loginId;

    private String key;

    @CheckSumInclude(order = 2)
    private String privateKey;

    @CheckSumInclude(order = 3)
    private String smartCardCode;

    @CheckSumInclude(order = 4)
    private String fee;

    @CheckSumInclude(order = 4)
    private String transactionRef;

    private String checksum;

    private String serviceId;

    @CheckSumInclude(order = 3)
    private String customerNo;

    private String customerName;

    List<String> productsCodes;

    @CheckSumInclude(order = 5)
    private String amount;

    private String invoicePeriod;
}
