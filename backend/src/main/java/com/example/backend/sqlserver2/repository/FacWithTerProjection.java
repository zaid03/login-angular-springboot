package com.example.backend.sqlserver2.repository;

import java.time.LocalDateTime;

public interface FacWithTerProjection {
    Integer getENT();
    String  getEJE();
    Integer getFACNUM();
    Integer getTERCOD();
    String  getCGECOD();
    String  getFACOBS();
    Double  getFACIMP();
    Double  getFACIEC();
    Double  getFACIDI();
    String  getFACTDC();
    Integer getFACANN();
    Integer getFACFAC();
    String  getFACDOC();
    LocalDateTime getFACDAT();
    LocalDateTime getFACFCO();
    String  getFACADO();
    String  getFACTXT();
    LocalDateTime getFACFRE();
    String  getCONCTP();
    String  getCONCPR();
    String  getCONCCR();
    Integer getFACOCT();
    String  getFACFPG();
    String  getFACOPG();
    String  getFACTPG();
    Double  getFACDTO();

    // fields from TER
    String  getTERNOM();
    String  getTERNIF();
}