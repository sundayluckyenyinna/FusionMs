/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Entity
@Table(name = "app_user")
@SuppressWarnings("PersistenceUnitPresent")
public class AppUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;
    @Column(name = "password", nullable = false, length = 120)
    private String password;
    @Column(name = "created_by", nullable = true, length = 80)
    private String createdBy;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_by", nullable = true, length = 20)
    private String updatedBy;
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt = null;
    @Column(name = "locked", nullable = false)
    private boolean isLocked = false;
    @Column(name = "expired", nullable = false)
    private boolean isExpired = false;
    @Column(name = "enabled", nullable = false)
    private boolean isEnabled = true;
    @Column(name = "channel", nullable = false, length = 20)
    private String channel;
    @Column(name = "password_change_date", nullable = true)
    private LocalDate passwordChangeDate = null;
    @Column(name = "encryption_key", nullable = false, length = 120)
    private String encryptionKey;
    @Column(name = "pay_account_open_bonus", nullable = false)
    private boolean payAccountOpenBonus = false;
    @Column(name = "new_acct_bonus", nullable = true)
    private BigDecimal accountOpenBonus = BigDecimal.ZERO;
    @Column(name = "account_number", nullable = true, length = 20)
    private String accountNumber = "";
    @Column(name = "auth_device", nullable = false)
    private boolean authenticateDevice = false;
    @Column(name = "tcred", nullable = true, length = 120)
    private String tcred;
    @Column(name = "ecred", nullable = true, length = 120)
    private String ecred;
    @Column(name = "padding", nullable = true, length = 20)
    private String padding = "AES/CBC/PKCS5Padding";
    @Column(name = "two_factor_amount", nullable = true)
    private BigDecimal twofactorAmount = BigDecimal.valueOf(100000.00);
}
