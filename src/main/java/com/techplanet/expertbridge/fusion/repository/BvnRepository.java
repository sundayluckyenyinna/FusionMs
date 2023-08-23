/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.repository;

import com.techplanet.expertbridge.fusion.model.Bvn;

/**
 *
 * @author Daniel Ofoleta
 */
public interface BvnRepository {

    public Bvn createBvn(Bvn oBvn);

    public Bvn getBvnDetails(String bvn);

}
