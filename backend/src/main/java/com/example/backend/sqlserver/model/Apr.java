package com.example.backend.sqlserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "APR", schema = "dbo")
public class Apr {
    
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private Integer TERCOD;

    @Column(nullable = false)
    private String AFACOD;

    @Column(nullable = false)
    private String ASUCOD;

    @Column(nullable = false)
    private String ARTCOD;

    @Column(nullable = false)
    private String APRREF;

    @Column(nullable = false)
    private Double APRPRE;

    @Column(nullable = false)
    private Double APRUEM;

    @Column(nullable = false)
    private String APROBS;


    public Integer getENT() {
        return ENT;
    }
    public void setENT(Integer ENT) {
        this.ENT = ENT;
    }

    public Integer getTERCOD() {
        return TERCOD;
    }
    public void setTERCOD(Integer TERCOD) {
        this.TERCOD = TERCOD;
    }

    public String getAFACOD() {
        return AFACOD;
    }
    public void setAFACOD(String AFACOD) {
        this.AFACOD = AFACOD;
    }

    public String getASUCOD() {
        return ASUCOD;
    }
    public void setASUCOD(String ASUCOD) {
        this.ASUCOD = ASUCOD;
    }

    public String getARTCOD() {
        return ARTCOD;
    }
    public void setARTCOD(String ARTCOD) {
        this.ARTCOD = ARTCOD;
    }

    public String getAPRREF() {
        return APRREF;
    }
    public void setAPRREF(String APRREF) {
        this.APRREF = APRREF;
    }

    public Double getAPRPRE() {
        return APRPRE;
    }
    public void setAPRPRE(Double APRPRE) {
        this.APRPRE = APRPRE;
    }

    public Double getAPRUEM() {
        return APRUEM;
    }
    public void setAPRUEM(Double APRUEM) {
        this.APRUEM = APRUEM;
    }

    public String getAPROBS() {
        return APROBS;
    }
    public void setAPROBS(String APROBS) {
        this.APROBS = APROBS;
    }
}
