package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.model.AppUser;
import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class EmailDetailsPayload {

    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    private AppUser appUser;
}
