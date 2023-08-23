/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.payload;

import java.util.List;
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
public class SMSRequestPayload {

 private List messages;
}
