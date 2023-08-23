
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.repository;

import com.netflix.discovery.shared.Pair;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.model.AppUser;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Daniel Ofoleta
 */
@Repository
@Transactional
public class CustomerRepositoryImpl implements CustomerRepository {



    @PersistenceContext
    EntityManager em;

       @Override
    public AppUser getAppUserUsingUsername(String userName) {
        TypedQuery<AppUser> query = em.createQuery("SELECT t FROM AppUser t WHERE t.userName = :userName", AppUser.class)
                .setParameter("userName", userName);
        List<AppUser> record = query.getResultList();
        if (record.isEmpty()) {
            return null;
        }
        return record.get(0);
    }
    
//     @Override
//    public Pair<CustomerPayload, String> getCustomerUsingMobileNumber(AppUser oAppUser, String mobileNumber) {
//        TypedQuery<Customer> query = em.createQuery("SELECT t FROM Customer t WHERE t.mobileNumber = :mobileNumber", Customer.class)
//                .setParameter("mobileNumber", mobileNumber);
//        List<Customer> record = query.getResultList();
//        if (record.isEmpty()) {
//            return null;
//        }
//        CustomerPayload oCustomerPayload = gson.fromJson(gson.toJson(record.get(0)), CustomerPayload.class);
//        oCustomerPayload.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
//        return new Pair(oCustomerPayload, ResponseCode.SUCCESS_CODE.getResponseCode());
//    }

}
