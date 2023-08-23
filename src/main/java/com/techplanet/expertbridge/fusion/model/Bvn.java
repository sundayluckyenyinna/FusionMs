/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techplanet.expertbridge.fusion.model;

import java.io.Serializable;
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
@Table(name = "bvn")
public class Bvn implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "created_at", nullable=true)
    private LocalDateTime createdAt= LocalDateTime.now();
    @Column(name = "bvn", nullable=false, length = 11)
    private String bvn;
    @Column(name = "first_name", nullable=true, length = 35)
    private String firstName;
    @Column(name = "last_name", nullable=true, length = 35)
    private String lastName;
    @Column(name = "middle_name", nullable=true, length = 35)
    private String middleName;
    @Column(name = "birth_date", nullable=true)
    private LocalDate birthDate;
    @Column(name = "gender", nullable=true, length = 10)
    private String gender;
    @Column(name = "photo", nullable=true, length = 30000, columnDefinition = "text")
    private String photo;
    @Column(name = "mobile_number", nullable=true, length = 13)
    private String mobileNumber;
    
}
