package com.example.backend.dto;


public class PuaEntDTO  {
    private String USUCOD;
    private int APLCOD;
    private int ENTCOD;
    private String PERCOD;
    private String ENTNOM;

    public PuaEntDTO (String USUCOD, int APLCOD, int ENTCOD, String PERCOD, String ENTNOM) {
        this.USUCOD = USUCOD;
        this.APLCOD = APLCOD;
        this.ENTCOD = ENTCOD;
        this.PERCOD = PERCOD;
        this.ENTNOM = ENTNOM;
    }

    public String getUSUCOD() {
        return USUCOD;
    }
    public void setUSUCOD(String USUCOD) {
        this.USUCOD = USUCOD;
    }

    public int getAPLCOD() {
        return APLCOD;
    }
    public void setAPLCOD(int APLCOD) {
        this.APLCOD = APLCOD;
    }

    public int getENTCOD() {
        return ENTCOD;
    }
    public void setENTCOD(int ENTCOD) {
        this.ENTCOD = ENTCOD;
    }

    public String getPERCOD() {
        return PERCOD;
    }
    public void setPERCOD(String PERCOD) {
        this.PERCOD = PERCOD;
    }

    public String getENTNOM() {
        return ENTNOM;
    }
    public void setENTNOM(String ENTNOM) {
        this.ENTNOM = ENTNOM;
    }

}