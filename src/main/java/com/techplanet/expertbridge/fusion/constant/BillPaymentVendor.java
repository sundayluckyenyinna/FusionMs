package com.techplanet.expertbridge.fusion.constant;

public enum BillPaymentVendor
{

    CREDIT_SWITCH("Credit-Switch");
    private final String customName;
    BillPaymentVendor(String customName){
        this.customName = customName;
    }
    public String getCustomName(){
        return this.customName;
    }
}
