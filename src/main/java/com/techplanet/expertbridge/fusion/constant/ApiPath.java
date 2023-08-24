/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.constant;

/**
 *
 * @author Daniel Ofoleta
 */
public class ApiPath {

    public static final String BVN_DETAILS = "/bvn/details";
   
    public static final String HEADER_STRING = "Authorization";
    //T
    public static final String TOKEN_PREFIX = "Bearer ";


    // ###################### CREDIT SWITCH RELATIVE URLS ########################
    // SMS
//    public static final String CS_SEND_SMS = "/sendsms";

    // AIRTIME AND DATA
    public static final String CS_BUY_AIRTIME = "/mvend";
    public static final String CS_BUY_DATA = "/dvend";
    public static final String CS_DATA_PLANS = "/mdataplans";

    // ELECTRICITY
    public static final String CS_VALIDATE_ELECTRICITY = "/evalidate";
    public static final String CS_ELECTRICITY_PAYMENT = "/evend";

    // CABLE TV
    public static final String CS_VALIDATE_SINGLE_CHOICE_CABLE = "/starvalidate1";
    public static final String CS_VALIDATE_MULTI_CHOICE_CABLE = "/cabletv/multichoice/validate";
    public static final String CS_CABLE_TV_PRODUCT = "/cabletv/multichoice/fetchproducts";
    public static final String CS_SINGLE_CABLE_TV_PAYMENT = "/starvend1";
    public static final String CS_MULTI_CHOICE_CABLE_TV_PAYMENT = "/cabletv/multichoice/vend";

    // BETTING
    public static final String CS_BETTING_PROVIDERS = "/betting/providers?loginId=%s&key=%s";
    public static final String CS_BET_ACCOUNT_VALIDATION = "/betting/validate";
    public static final String CS_BETTING_PAY = "/betting/pay";


    // ################################# Client Request Endpoint ########################### //
    
    public static final String AIRTIME = "/airtime/purchase";
    public static final String DATA = "/data/purchase";
    public static final String DATA_PLANS = "/data/plans";
    public static final String VALIDATE_ELECTRICITY_ = "/electricity/validate";
    public static final String PAY_ELECTRICITY = "/electricity/pay-bill";
    public static final String ELECTRICITY_BILLERS = "/electricity/billers";
    public static final String VALIDATE_CABLE_BILL = "/cabletv/validate";
    public static final String PAY_CABLE_TV_BILL = "/cabletv/pay-bill";
    public static final String CABLE_TV_BILLERS = "/cabletv/billers";


    // Funds Transfer Endpoints
    public static final String EXPERT_BRIDGE_BASE_URL = "";
    
    // Messaging Service
    public static final String SEND_EMAIL_URL = "/email/send";
    public static final String SEND_SMS_URL = "/sms/send";

    // BETTING
    public static final String BETTING = "/betting/providers";
    public static final String BET_ACCOUNT_VALIDATION = "/betting/account-validation";
    public static final String BET_ACCOUNT_FUNDING = "/betting/account-funding";
    
}
