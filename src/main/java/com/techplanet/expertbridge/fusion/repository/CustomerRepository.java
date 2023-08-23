/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.repository;

import com.netflix.discovery.shared.Pair;
import com.techplanet.expertbridge.fusion.model.AppUser;

/**
 *
 * @author Daniel Ofoleta
 */
public interface CustomerRepository {

    AppUser getAppUserUsingUsername(String username);
//    public Pair<CustomerPayload, String> getCustomerUsingMobileNumber(AppUser oAppUser, String mobileNumber);
   
}
