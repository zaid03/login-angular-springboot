package com.example.backend.sqlserver2.model;

import jakarta.persistence.*;

@Entity
@IdClass(CfgId.class)
@Table(name = "CFG", schema = "dbo")
public class Cfg {

    @Id
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private String EJE;

    @Column(nullable = false)
    private Integer CFGEST;

    //getters and setters

    public Integer getENT() {
        return ENT;
    }
    public void setENT(Integer ENT) {
        this.ENT = ENT;
    }

    public String getEJE() {
        return EJE;
    }
    public void setEJE(String EJE) {
        this.EJE = EJE;
    }

    public Integer getCFGEST() {
        return CFGEST;
    }
    public void setCFGEST(Integer CFGEST) {
        this.CFGEST = CFGEST;
    }
}