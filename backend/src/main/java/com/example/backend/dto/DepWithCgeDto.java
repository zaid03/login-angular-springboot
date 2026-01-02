package com.example.backend.dto;

public class DepWithCgeDto {
    private String depcod;
    private String depdes;
    private Integer depalm;
    private Integer depcom;
    private Integer depint;
    private String cgecod;
    private String cgedes;

    public DepWithCgeDto(String depcod, String depdes, Integer depalm, Integer depcom, Integer depint, String cgecod, String cgedes) {
        this.depcod = depcod;
        this.depdes = depdes;
        this.depalm = depalm;
        this.depcom = depcom;
        this.depint = depint;
        this.cgecod = cgecod;
        this.cgedes = cgedes;
    }

    public String getDepcod() { return depcod; }
    public void setDepcod(String depcod) { this.depcod = depcod; }

    public String getDepdes() { return depdes; }
    public void setDepdes(String depdes) { this.depdes = depdes; }

    public Integer getDepalm() { return depalm; }
    public void setDepalm(Integer depalm) { this.depalm = depalm; }

    public Integer getDepcom() { return depcom; }
    public void setDepcom(Integer depcom) { this.depcom = depcom; }

    public Integer getDepint() { return depint; }
    public void setDepint(Integer depint) { this.depint = depint; }

    public String getCgecod() { return cgecod; }
    public void setCgecod(String cgecod) { this.cgecod = cgecod; }

    public String getCgedes() { return cgedes; }
    public void setCgedes(String cgedes) { this.cgedes = cgedes; }
}