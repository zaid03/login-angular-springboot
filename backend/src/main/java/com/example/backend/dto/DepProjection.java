package com.example.backend.dto;

public class DepProjection {
    private String CGECOD;
    private String CGEDES;
    private Integer CGECIC;
    private Integer DEPINT;
    private Integer DEPALM;
    private Integer DEPCOM;

    public DepProjection(String CGECOD, String CGEDES, Integer CGECIC, Integer DEPINT, Integer DEPALM, Integer DEPCOM) {
        this.CGECOD = CGECOD;
        this.CGEDES = CGEDES;
        this.CGECIC = CGECIC;
        this.DEPINT = DEPINT;
        this.DEPALM = DEPALM;
        this.DEPCOM = DEPCOM;
    }

    public String getCGECOD() { return CGECOD; }
    public void setCGECOD(String CGECOD) { this.CGECOD = CGECOD; }

    public String getCGEDES() { return CGEDES; }
    public void setCGEDES(String CGEDES) { this.CGEDES = CGEDES; }

    public Integer getCGECIC() { return CGECIC; }
    public void setCGECIC(Integer CGECIC) { this.CGECIC = CGECIC; }

    public Integer getDEPINT() { return DEPINT; }
    public void setDEPINT(Integer DEPINT) { this.DEPINT = DEPINT; }
    
    public Integer getDEPALM() { return DEPALM; }
    public void setDEPALM(Integer DEPALM) { this.DEPALM = DEPALM; }

    public Integer getDEPCOM() { return DEPCOM; }
    public void setDEPCOM(Integer DEPCOM) { this.DEPCOM = DEPCOM; }
}