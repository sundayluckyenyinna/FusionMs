package com.techplanet.expertbridge.fusion.dto.creditswitch.request;

import com.techplanet.expertbridge.fusion.annotation.CheckSumInclude;
import lombok.Data;

import java.util.List;

@Data
public class SmsRequestDTO
{
    @CheckSumInclude(order = 1)
    private String loginId;

    @CheckSumInclude(order = 2)
    private String privateKey;

    private String key;
    private String senderId;
    private List<String> msisdn;
    private double amount;
    private String messageBody;

    @CheckSumInclude(order = 3)
    private String transactionRef;
    private String checksum;
}
