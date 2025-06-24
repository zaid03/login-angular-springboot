package com.example.backend.dto;

public class AprDto {

    private Integer ENT;
    private Integer TERCOD;
    private String AFACOD;
    private String ASUCOD;
    private String ARTCOD;
    private String APRREF;
    private Double APRPRE;
    private Double APRUEM;
    private String APROBS;
    private Integer APRACU;

    public AprDto(Integer ENT, Integer TERCOD, String AFACOD, String ASUCOD, String ARTCOD, String APRREF, Double APRPRE, Double APRUEM, String APROBS, Integer APRACU) {
        this.ENT = ENT;
        this.TERCOD = TERCOD;
        this.AFACOD = AFACOD;
        this.ASUCOD = ASUCOD;
        this.ARTCOD = ARTCOD;
        this.APRREF = APRREF;
        this.APRPRE = APRPRE;
        this.APRUEM = APRUEM;
        this.APROBS = APROBS;
        this.APRACU = APRACU; 
    }

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

    public Integer getAPRACU() {
        return APRACU;
    }
    public void setAPRACU(Integer APRACU) {
        this.APRACU = APRACU;
    }
}
