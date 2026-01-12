package com.example.backend.dto;

public class servicesPerPersons {
    private String PERCOD;
    private String PERNOM;
    private String DEPCOD;
    private String DEPDES;
    private Integer DEPALM;
    private Integer DEPCOM;
    private Integer DEPINT;
    private String CGECOD;
    private String CGEDES;

    public servicesPerPersons() {}

    public servicesPerPersons(String PERCOD, String PERNOM, String DEPCOD, String DEPDES, Integer DEPALM, Integer DEPCOM, Integer DEPINT, String CGECOD, String CGEDES) {
        this.PERCOD = PERCOD;
        this.PERNOM = PERNOM;
        this.DEPCOD = DEPCOD;
        this.DEPDES = DEPDES;
        this.DEPALM = DEPALM;
        this.DEPCOM = DEPCOM;
        this.DEPINT = DEPINT;
        this.CGECOD = CGECOD;
        this.CGEDES = CGEDES;
    }

    public String getPERCOD() {return PERCOD;}
    public void setPERCOD(String PERCOD) {this.PERCOD = PERCOD;}

    public String getPERNOM() {return PERNOM;}
    public void setPERNOM(String PERNOM) {this.PERNOM = PERNOM;}

    public String getDEPCOD() { return DEPCOD; }
    public void setDEPCOD(String DEPCOD) { this.DEPCOD = DEPCOD; }

    public String getDEPDES() {return DEPDES;}
    public void setDEPDES(String DEPDES) {this.DEPDES = DEPDES;}

    public Integer getDEPALM() {return DEPALM;}
    public void setDEPALM(Integer DEPALM) {this.DEPALM = DEPALM;}

    public Integer getDEPCOM() {return DEPCOM;}
    public void setDEPCOM(Integer DEPCOM) {this.DEPCOM = DEPCOM;}

    public Integer getDEPINT() {return DEPINT;}
    public void setDEPINT(Integer DEPINT) {this.DEPINT = DEPINT;}

    public String getCGECOD() {return CGECOD;}
    public void setCGECOD(String CGECOD) {this.CGECOD = CGECOD;}

    public String getCGEDES() {return CGEDES;}
    public void setCGEDES(String CGEDES) {this.CGEDES = CGEDES;}
}