package com.example.backend.sqlserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TER", schema = "dbo")
public class Ter {

    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private Integer TERCOD;

    @Column(nullable = false)
    private String TERNOM;

    @Column(nullable = false)
    private String TERALI;

    @Column(nullable = false)
    private String TERNIF;

    @Column(nullable = false)
    private String TERDOM;

    @Column(nullable = false)
    private String TERCPO;

    @Column(nullable = false)
    private String TERPOB;

    @Column(nullable = false)
    private String TERTEL;

    @Column(nullable = false)
    private String TERFAX;

    @Column(nullable = false)
    private String TERWEB;

    @Column(nullable = false)
    private String TERCOE;

    @Column(nullable = false)
    private String TEROBS;

    @Column(nullable = false)
    private Integer TERAYT;

    @Column(nullable = false)
    private Integer TERBLO;

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

    public String getTERNOM() {
        return TERNOM;
    }
    public void setTERNOM(String TERNOM) {
        this.TERNOM = TERNOM;
    }

    public String getTERALI() {
        return TERALI;
    }
    public void setTERALI(String TERALI) {
        this.TERALI = TERALI;
    }

    public String getTERNIF() {
        return TERNIF;
    }
    public void setTERNIF(String TERNIF) {
        this.TERNIF = TERNIF;
    }

    public String getTERDOM() {
        return TERDOM;
    }
    public void setTERDOM(String TERDOM) {
        this.TERDOM = TERDOM;
    }

    public String getTERCPO() {
        return TERCPO;
    }
    public void setTERCPO(String TERCPO) {
        this.TERCPO = TERCPO;
    }

    public String getTERPOB() {
        return TERPOB;
    }
    public void setTERPOB(String TERPOB) {
        this.TERPOB = TERPOB;
    }

    public String getTERTEL() {
        return TERTEL;
    }
    public void setTERTEL(String TERTEL) {
        this.TERTEL = TERTEL;
    }

    public String getTERFAX() {
        return TERFAX;
    }
    public void setTERFAX(String TERFAX) {
        this.TERFAX = TERFAX;
    }

    public String getTERWEB() {
        return TERWEB;
    }
    public void setTERWEB(String TERWEB) {
        this.TERWEB = TERWEB;
    }

    public String getTERCOE() {
        return TERCOE;
    }
    public void setTERCOE(String TERCOE) {
        this.TERCOE = TERCOE;
    }

    public String getTEROBS() {
        return TEROBS;
    }
    public void setTEROBS(String TEROBS) {
        this.TEROBS = TEROBS;
    }

    public Integer getTERAYT() {
        return TERAYT;
    }
    public void setTERAYT(Integer TERAYT) {
        this.TERAYT = TERAYT;
    }

    public Integer getTERBLO() {
        return TERBLO;
    }
    public void setTERBLO(Integer TERBLO) {
        this.TERBLO = TERBLO;
    }
}