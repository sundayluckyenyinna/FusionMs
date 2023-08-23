package com.techplanet.expertbridge.fusion.constant;

public enum CableTvType
{
    STAR_TIMES("start-times"),
    GOTV("gotv"),
    DSTV("dstv");

    private final String serviceCode;
    CableTvType(String serviceCode){
        this.serviceCode = serviceCode;
    }

    public String getServiceCode() { return this.serviceCode; }
}
