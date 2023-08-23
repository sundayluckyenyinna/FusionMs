package com.techplanet.expertbridge.fusion.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface GenericService
{
    String postForObject(String url, String requestJson, Map<String, String> headers, Map<String, Object> params);
    String postForObject(String url, String requestJson, Map<String, String> headers);
    String postForObject(String url, String requestJson, String token);
}
