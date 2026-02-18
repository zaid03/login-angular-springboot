package com.example.backend.dto;

import java.time.LocalDateTime;

public class FacturaConsultaRequestDto {
    private String org;
    private String ent;
    private String eje;
    private String usu;
    private String pwdSha1Base64;
    private String fechaUtc;
    private String nonce;
    private String tokenSha512;
    private String tokenSha1;
    private String tipoRegistro;
    private String cge;
    private Integer tipoDocumento;
    private String tercero;
    private LocalDateTime fecRegDesde;
    private LocalDateTime fecRegHasta;
    private String docProveedor;
    private LocalDateTime fecDocDesde;
    private LocalDateTime fecDocHasta;
    private String situacionIgual;
    private String estado;
    private String webserviceUrl;

    public String getOrg() {return org;}

    public String getEnt() {return ent;}

    public String getEje() {return eje;}

    public String getUsu() {return usu;}

    public String getPwdSha1Base64() {return pwdSha1Base64;}

    public String getFechaUtc() {return fechaUtc;}

    public String getNonce() {return nonce;}

    public String getTokenSha512() {return tokenSha512;}

    public String getTokenSha1() {return tokenSha1;}

    public String getTipoRegistro() {return tipoRegistro;}

    public String getCge() {return cge;}

    public Integer getTipoDocumento() {return tipoDocumento;}

    public String getTercero() {return tercero;}

    public LocalDateTime getFecRegDesde() {return fecRegDesde;}

    public LocalDateTime getFecRegHasta() {return fecRegHasta;}

    public String getDocProveedor() {return docProveedor;}

    public LocalDateTime getFecDocDesde() {return fecDocDesde;}

    public LocalDateTime getFecDocHasta() {return fecDocHasta;}

    public String getSituacionIgual() {return situacionIgual;}

    public String getEstado() {return estado;}

    public String getWebserviceUrl() {return webserviceUrl;}
}
