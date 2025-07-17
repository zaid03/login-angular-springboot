package com.example.backend.sqlserver2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;

@Entity
@IdClass(DpeId.class)
@Table(name = "DPE", schema = "dbo")
public class Dpe {
    @Id
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private String EJE;

    @Id
    @Column(nullable = false)
    private String DEPCOD;

    @Id
    @Column(nullable = false)
    private String Percod;

    public Integer getENT() {
        return ENT;
    }
    public void setENT(Integer ENT) {
        this.ENT = ENT;
    }

    public String getEJE() {
        return EJE;
    }
    public void setEJE(String EJE){
        this.EJE = EJE;
    }

    public String getDEPCOD() {
        return DEPCOD;
    }
    public void setDEPCOD(String DEPCOD) {
        this.DEPCOD = DEPCOD;
    }

    public String getPercod() {
        return Percod;
    }
    public void setPercod(String Percod) {
        this.Percod = Percod;
    }
}
