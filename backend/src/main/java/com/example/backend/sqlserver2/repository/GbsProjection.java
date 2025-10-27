package com.example.backend.sqlserver2.repository;

import java.time.LocalDateTime;

public interface GbsProjection {
    String getCGECOD();
    String getCGEDES();
    Integer getCGECIC();
    String getGBSREF();
    String getGBSOPE();
    String getGBSORG();
    String getGBSFUN();
    String getGBSECO();
    LocalDateTime getGBSFOP();
    Double getGBSIMP();
    Double getGBSIBG();
    Double getGBSIUS();
    Double getGBSICO();
    Double getGBSIUT();
    Double getGBSICT();
    Double getGBS413();
}