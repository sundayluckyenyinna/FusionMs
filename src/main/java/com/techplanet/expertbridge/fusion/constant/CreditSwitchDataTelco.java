package com.techplanet.expertbridge.fusion.constant;

public enum CreditSwitchDataTelco
{
    AIRTEL(CreditSwitchDataServiceCode.D01D),
    ETISALAT(CreditSwitchDataServiceCode.D02D),
    GLOBACOM(CreditSwitchDataServiceCode.D03D),
    MTN(CreditSwitchDataServiceCode.D04D),
    SMILE(CreditSwitchDataServiceCode.D05D),
    NTEL(CreditSwitchDataServiceCode.D06D);
    private final CreditSwitchDataServiceCode serviceCode;
    CreditSwitchDataTelco(CreditSwitchDataServiceCode serviceCode){
        this.serviceCode = serviceCode;
    }
    public String getServiceCode() {
        return this.serviceCode.toString();
    }
}

enum CreditSwitchDataServiceCode{
    D01D, D02D, D03D, D04D, D05D, D06D
}