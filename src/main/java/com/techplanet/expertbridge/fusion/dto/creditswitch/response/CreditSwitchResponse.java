package com.techplanet.expertbridge.fusion.dto.creditswitch.response;

import lombok.Data;

@Data
public class CreditSwitchResponse
{
    private boolean isError;
    private String successResponse;
    private String errorResponse;
}
