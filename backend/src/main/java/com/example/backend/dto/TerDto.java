package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TerDto {

    @JsonProperty("TERWEB")
    private String TERWEB;

    @JsonProperty("TEROBS")
    private String TEROBS;

    @JsonProperty("TERBLO")
    private Integer TERBLO;

    @JsonProperty("TERACU")
    private Integer TERACU;

    @JsonProperty("TERNOM")
    private String TERNOM;

    @JsonProperty("TERALI")
    private String TERALI;

    @JsonProperty("TERNIF")
    private String TERNIF;

    public String getTERWEB() { return TERWEB; }
    public void setTERWEB(String TERWEB) { this.TERWEB = TERWEB; }

    public String getTEROBS() { return TEROBS; }
    public void setTEROBS(String TEROBS) { this.TEROBS = TEROBS; }

    public Integer getTERBLO() { return TERBLO; }
    public void setTERBLO(Integer TERBLO) { this.TERBLO = TERBLO; }

    public Integer getTERACU() { return TERACU; }
    public void setTERACU(Integer TERACU) { this.TERACU = TERACU; }

    public String getTERNOM() { return TERNOM; }
    public void setTERNOM(String TERNOM) { this.TERNOM = TERNOM; }

    public String getTERALI() { return TERALI; }
    public void setTERALI(String TERALI) { this.TERALI = TERALI; }

    public String getTERNIF() { return TERNIF; }
    public void setTERNIF(String TERNIF) { this.TERNIF = TERNIF; }
}
