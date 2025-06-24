package com.example.backend.sqlserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ART", schema = "dbo")
public class Art {
    
    @Column(nullable = false)
    private Integer ENT;

    @Column(nullable = false)
    private String AFACOD;

    @Column(nullable = false)
    private String ASUCOD;

    @Id
    @Column(nullable = false)
    private String ARTCOD;

    @Column(nullable = true)
    private String ARTDES;

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

    public String getARTCOD() {
        return ARTCOD;
    }
    public void setARTCOD(String ARTCOD) {
        this.ARTCOD = ARTCOD;
    }

    public String getARTDES() {
        return ARTDES;
    }
    public void setARTDES(String ARTDES) {
        this.ARTDES = ARTDES;
    }
}
