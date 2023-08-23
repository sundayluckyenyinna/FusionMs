package com.techplanet.expertbridge.fusion.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biller_lookup_data")
@Getter
@Setter
public class BillerLookupData
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "biller_id")
    private String billerId;              // E.g ELECTRICITY

    @Column(name = "biller_name")
    private String billerName;          // E.g Ikeja Electric Disco(Prepaid Account)

    @Column(name = "biller_desc")
    private String billerDescription;   // E.g Biller around Ikeja area

    @Column(name = "biller_short_hand")
    private String billerShorthand;        // E.g ELE

    @Column(name = "vendor_name")
    private String vendorName;                // E.g CREDIT_SWITCH

    @Column(name = "vendor_short_code")
    private String vendorShortCode;           // E.g CS

    @Column(name = "biller_code")
    private String billerCode;                      // E.g E01E

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
