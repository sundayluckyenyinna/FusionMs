package com.techplanet.expertbridge.fusion.payload;

import com.techplanet.expertbridge.fusion.dto.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
public class BvnResponsePayload {
    private String status;
    private Data data;
}
