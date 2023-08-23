package com.techplanet.expertbridge.fusion.constant;

public enum CreditSwitchAirtimeTelco
{
    AIRTEL(CreditSwitchAirtimeServiceCode.A01E),
    ETISALAT(CreditSwitchAirtimeServiceCode.A02E),
    GLOBACOM(CreditSwitchAirtimeServiceCode.A03E),
    MTN(CreditSwitchAirtimeServiceCode.A04E);

    private final CreditSwitchAirtimeServiceCode serviceCode;
    CreditSwitchAirtimeTelco(CreditSwitchAirtimeServiceCode serviceCode){
        this.serviceCode = serviceCode;
    }
    public String getServiceId(){
        return this.serviceCode.name();
    }
}

enum CreditSwitchAirtimeServiceCode{
    A01E, A02E, A03E, A04E
}