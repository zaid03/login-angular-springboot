package com.example.backend.sqlserver2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;

@Entity
@IdClass(AsuId.class)
@Table(name = "ASU", schema = "dbo")
public class Asu {

    @Id
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private String AFACOD;

    @Id
    @Column(nullable = false)
    private String ASUCOD;

    @Column(nullable = false)
    private String ASUDES;

    public Integer getENT() {
        return ENT;
    }
    public void setENT(Integer ENT) {
        this.ENT = ENT;
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

    public String getASUDES() {
        return ASUDES;
    }
    public void setASUDES(String ASUDES) {
        this.ASUDES = ASUDES;
    }
}
