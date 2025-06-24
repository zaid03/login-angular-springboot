package com.example.backend.dto;

public class TpeDto {

    private Integer TERCOD;

    private String TPENOM;

    private String TPETEL;

    private String TPETMO;

    private String TPECOE;

    private String TPEOBS;

    public TpeDto(Integer TERCOD, String TPENOM, String TPETEL, String TPETMO, String TPECOE, String TPEOBS) {
        this.TERCOD = TERCOD;
        this.TPENOM = TPENOM;
        this.TPETEL = TPETEL;
        this.TPETMO = TPETMO;
        this.TPECOE = TPECOE;
        this.TPEOBS = TPEOBS;
    }

    
    public Integer getTERCOD() { 
        return TERCOD; 
    }
    public void setTERCOD(Integer TERCOD) { 
        this.TERCOD = TERCOD; 
    }

    public String getTPENOM() { 
        return TPENOM; 
    }
    public void setTPENOM(String TPENOM) { 
        this.TPENOM = TPENOM; 
    }

    public String getTPETEL() { 
        return TPETEL; 
    }
    public void setTPETEL(String TPETEL) { 
        this.TPETEL = TPETEL; 
    }

    public String getTPETMO() { 
        return TPETMO; 
    }
    public void setTPETMO(String TPETMO) { 
        this.TPETMO = TPETMO; 
    }

    public String getTPECOE() { 
        return TPECOE; 
    }
    public void setTPECOE(String TPECOE) { 
        this.TPECOE = TPECOE; 
    }
    
    public String getTPEOBS() { 
        return TPEOBS; 
    }
    public void setTPEOBS(String TPEOBS) { 
        this.TPEOBS = TPEOBS; 
    }
}