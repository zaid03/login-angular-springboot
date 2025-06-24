package com.example.backend.sqlserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AFA", schema = "dbo")
public class Afa {
    
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private String AFACOD;

    @Column(nullable = true)
    private String AFADES;

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

    public String getAFADES() {
        return AFADES;
    }
    public void setAFADES(String AFADES) {
        this.AFADES = AFADES;
    }
}
