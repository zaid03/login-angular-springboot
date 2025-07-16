package com.example.backend.sqlserver2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(TpeId.class)
@Table(name = "TPE", schema = "dbo")
public class Tpe {
    
    @Id
    @Column(name = "ENT", nullable = false)
    private Integer ent;

    @Id
    @Column(name = "TERCOD", nullable = false)
    private Integer tercod;

    @Id
    @Column(name = "TPECOD", nullable = false)
    private Integer tpecod;

    @Column(name = "TPENOM")
    private String tpenom;

    @Column(name = "TPETEL")
    private String tpetel;

    @Column(name = "TPETMO")
    private String tpetmo;

    @Column(name = "TPECOE")
    private String tpecoe;

    @Column(name = "TPEOBS")
    private String tpeobs;


    public Integer getent() {
        return ent;
    }
    public void setent(Integer ent) {
        this.ent = ent;
    }

    public Integer gettercod() {
        return tercod;
    }
    public void settercod(Integer tercod) {
        this.tercod = tercod;
    }

    public Integer gettpecod() { 
        return tpecod;
    }

    public void settpecod(Integer tpecod) { 
        this.tpecod = tpecod; 
    }

    public String gettpenom() {
        return tpenom;
    }
    public void settpenom(String tpenom) {
        this.tpenom = tpenom;
    }

    public String gettpetel() {
        return tpetel;
    }
    public void settpetel(String tpetel) {
        this.tpetel = tpetel;
    }

    public String gettpetmo() {
        return tpetmo;
    }
    public void settpetmo(String tpetmo) {
        this.tpetmo = tpetmo;
    }

    public String gettpecoe() {
        return tpecoe;
    }
    public void settpecoe(String tpecoe) {
        this.tpecoe = tpecoe;
    }

    public String gettpeobs() {
        return tpeobs;
    }
    public void settpeobs(String tpeobs) {
        this.tpeobs = tpeobs;
    }
}
