package com.techplanet.expertbridge.fusion.security;

import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.exception.ExceptionResponse;
import com.techplanet.expertbridge.fusion.model.AppUser;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.PlainTextPayload;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

/**
 *
 * @author dofoleta
 */
@Service
public class AesServiceImpl implements AesService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    Gson gson;

    @Override
    public String encrypt(String plaintext, AppUser appUser)  {
        try {
            String secret = appUser.getEcred().split("/")[0];
            String iv = appUser.getEcred().split("/")[1];
            String padding = appUser.getPadding();
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(padding);
            
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            
            //Perform Encryption
            return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String decrypt(String cipherText, AppUser appUser) {
        try {
            String secret = appUser.getEcred().split("/")[0];
            String iv = appUser.getEcred().split("/")[1];
            String padding = appUser.getPadding();
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            
            Cipher cipher = Cipher.getInstance(padding);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            
            //Perform Decryption
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public PlainTextPayload decrypt(GenericPayload genericRequestPayload, AppUser appUser) {

        String encryptedRequest = genericRequestPayload.getRequest();
        String painTextRequest;

        PlainTextPayload oPlainTextPayload = new PlainTextPayload(); 
        try {
            painTextRequest = decrypt(encryptedRequest, appUser);
            if (painTextRequest == null) {
                oPlainTextPayload.setError(true);

                ExceptionResponse exResponse = new ExceptionResponse();
                exResponse.setResponseCode(ResponseCode.NO_ROLE.getResponseCode());
                exResponse.setResponseMessage(messageSource.getMessage("appMessages.encryption", new Object[0], Locale.ENGLISH));
                String exceptionJson = gson.toJson(exResponse);

                GenericPayload responsePayload = new GenericPayload();
                responsePayload.setResponse(encrypt(exceptionJson, appUser));

                oPlainTextPayload.setResponse(gson.toJson(responsePayload));
            } else {
                oPlainTextPayload.setError(false);
                oPlainTextPayload.setResponse("SUCCESS");
                oPlainTextPayload.setPlainTextPayload(painTextRequest);
                oPlainTextPayload.setAppUser(appUser);
            }
        } catch (NoSuchMessageException ex) {
            try {
                oPlainTextPayload.setError(true);
                String errorMessage = ex.getMessage();
                ExceptionResponse exResponse = new ExceptionResponse();
                exResponse.setResponseCode(ResponseCode.FORMAT_EXCEPTION.getResponseCode());
                exResponse.setResponseMessage(errorMessage);
                String exceptionJson = gson.toJson(exResponse);

                GenericPayload responsePayload = new GenericPayload();
                responsePayload.setResponse(encrypt(exceptionJson, appUser));

                return oPlainTextPayload;
            } catch (Exception ex1) {
                Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (Exception ex) {
            Logger.getLogger(AesServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return oPlainTextPayload;
    }
}
