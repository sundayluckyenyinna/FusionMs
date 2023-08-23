package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class AirtimeDataResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private String mReference;
    private Long tranxReference;
    private String recipient;
    private double amount;
    private String confirmCode;
    private String network;
    private String tranxDate;
}
