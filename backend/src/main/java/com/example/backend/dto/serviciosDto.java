package com.example.backend.dto;

public class serviciosDto {
    private String DEPCOD;
    private String DEPDES;
    private Integer DEPALM;
    private Integer DEPCOM;
    private Integer DEPINT;
    private String CCOCOD;
    private String CGECOD;

    public serviciosDto(String DEPCOD, String DEPDES, Integer DEPALM, Integer DEPCOM, Integer DEPINT, String CCOCOD, String CGECOD) {
        this.DEPCOD = DEPCOD;
        this.DEPDES = DEPDES;
        this.DEPALM = DEPALM;
        this.DEPCOM = DEPCOM;
        this.DEPINT = DEPINT;
        this.CCOCOD = CCOCOD;
        this.CGECOD = CGECOD;
    }

    public String getDEPCOD() { return DEPCOD; }
    public void setDEPCOD(String DEPCOD) { this.DEPCOD = DEPCOD; }

    public String getDEPDES() { return DEPDES; }
    public void setDEPDES(String DEPDES) { this.DEPDES = DEPDES; }

    public Integer getDEPALM() { return DEPALM; }
    public void setDEPALM(Integer DEPALM) { this.DEPALM = DEPALM; }

    public Integer getDEPCOM() { return DEPCOM; }
    public void setDEPCOM(Integer DEPCOM) { this.DEPCOM = DEPCOM; }

    public Integer getDEPINT() { return DEPINT; }
    public void setDEPINT(Integer DEPINT) { this.DEPINT = DEPINT; }

    public String getCCOCOD() { return CCOCOD; }
    public void setCCOCOD(String CCOCOD) { this.CCOCOD = CCOCOD; }

    public String getCGECOD() { return CGECOD; }
    public void setCGECOD(String CGECOD) { this.CGECOD = CGECOD; }
}
