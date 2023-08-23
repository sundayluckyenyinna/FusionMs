/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Daniel Ofoleta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OmniResponsePayload {

    private String responseCode;
    private String responseMessage;
}
