package com.techplanet.expertbridge.fusion.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
public class MessageDTO {
    private List destinations;
    private String from;
    private String text;
}
