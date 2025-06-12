package com.example.backend.sqlserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TPE", schema = "dbo")
public class Tpe {
    
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private Integer TERCOD;

    @Column(nullable = false)
    private String TPENOM;

    @Column(nullable = false)
    private String TPETEL;

    @Column(nullable = false)
    private String TPETMO;

    @Column(nullable = false)
    private String TPECOE;

    @Column(nullable = false)
    private String TPEOBS;


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

    public String getTPENOM() {
        return TPENOM;
    }
    public void setTPENOM(String TPENOM) {
        this.TPENOM = TPENOM;
    }

    public String getTPETEL() {
        return TPETEL;
    }
    public void setTPETEL(String TPETEL) {
        this.TPETEL = TPETEL;
    }

    public String getTPETMO() {
        return TPETMO;
    }
    public void setTPETMO(String TPETMO) {
        this.TPETMO = TPETMO;
    }

    public String getTPECOE() {
        return TPECOE;
    }
    public void setTPECOE(String TPECOE) {
        this.TPECOE = TPECOE;
    }

    public String getTPEOBS() {
        return TPEOBS;
    }
    public void setTPEOBS(String TPEOBS) {
        this.TPEOBS = TPEOBS;
    }
}
