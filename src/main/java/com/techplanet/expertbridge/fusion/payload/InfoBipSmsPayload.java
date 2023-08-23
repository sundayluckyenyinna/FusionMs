package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class InfoBipSmsPayload {
    private String recipient;
    private String textMessage;
    private AppUser appUser;
}
