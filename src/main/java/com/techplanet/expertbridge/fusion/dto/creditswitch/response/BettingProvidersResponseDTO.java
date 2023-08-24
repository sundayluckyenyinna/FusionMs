package com.techplanet.expertbridge.fusion.dto.creditswitch.response;


import lombok.Data;

import java.util.HashMap;

@Data
public class BettingProvidersResponseDTO
{
    private String statusCode;
    private String statusDescription;
    private HashMap<String, String> result;
}
