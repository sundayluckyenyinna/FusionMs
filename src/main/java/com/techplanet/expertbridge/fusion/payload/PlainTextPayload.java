package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

/**
 *
 * @author dakinkuolie
 */
@Data
public class PlainTextPayload {
    private boolean error;
    private String response;
    private String plainTextPayload;
    private AppUser appUser;
}
