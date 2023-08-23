package com.techplanet.expertbridge.fusion.security;

import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.PlainTextPayload;



/**
 *
 * @author dofoleta
 */
public interface AesService {

    public PlainTextPayload decrypt(GenericPayload genericRequestPayload, AppUser appUser);

    public String decrypt(String cipherText, AppUser appUser);

    public String encrypt(String plaintext, AppUser appUser);
}
