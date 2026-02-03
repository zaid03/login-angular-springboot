package com.example.backend.service;

public interface CotContratoProjection {
    ConInfo getCon();
    TerInfo getTer();

    interface ConInfo {
        Integer getCONCOD();
        String getCONLOT();
        String getCONDES();
        java.time.LocalDateTime getCONFIN();
        java.time.LocalDateTime getCONFFI();
        Integer getCONBLO();
    }

    interface TerInfo {
        Integer getTERCOD();
        String getTERNOM();
    }
}
