package com.example.backend.dto;

import java.time.LocalDateTime;

public class FacWithTerDto {
    private Integer ent;
    private String eje;
    private Integer facnum;
    private Integer tercod;
    private String cgecod;
    private String facobs;
    private Double facimp;
    private Double faciec;
    private Double facidi;
    private String factdc;
    private String facann;
    private String facfac;
    private String facdoc;
    private LocalDateTime facdat;    
    private LocalDateTime facfco;      
    private String facado;           
    private String factxt;
    private LocalDateTime facfre;
    private String conctp;
    private String concpr;
    private String conccr;
    private Integer facoct;
    private String facfpg;
    private String facopg;             
    private String factpg;
    private Double facdto;
    private String ternom;
    private String ternif;

    private FacWithTerDto(Builder builder) {
        this.ent = builder.ent;
        this.eje = builder.eje;
        this.facnum = builder.facnum;
        this.tercod = builder.tercod;
        this.cgecod = builder.cgecod;
        this.facobs = builder.facobs;
        this.facimp = builder.facimp;
        this.faciec = builder.faciec;
        this.facidi = builder.facidi;
        this.factdc = builder.factdc;
        this.facann = builder.facann;
        this.facfac = builder.facfac;
        this.facdoc = builder.facdoc;
        this.facdat = builder.facdat;
        this.facfco = builder.facfco;
        this.facado = builder.facado;
        this.factxt = builder.factxt;
        this.facfre = builder.facfre;
        this.conctp = builder.conctp;
        this.concpr = builder.concpr;
        this.conccr = builder.conccr;
        this.facoct = builder.facoct;
        this.facfpg = builder.facfpg;
        this.facopg = builder.facopg;
        this.factpg = builder.factpg;
        this.facdto = builder.facdto;
        this.ternom = builder.ternom;
        this.ternif = builder.ternif;
    }

    public static class Builder {
        private Integer ent;
        private String eje;
        private Integer facnum;
        private Integer tercod;
        private String cgecod;
        private String facobs;
        private Double facimp;
        private Double faciec;
        private Double facidi;
        private String factdc;
        private String facann;
        private String facfac;
        private String facdoc;
        private LocalDateTime facdat;
        private LocalDateTime facfco;
        private String facado;
        private String factxt;
        private LocalDateTime facfre;
        private String conctp;
        private String concpr;
        private String conccr;
        private Integer facoct;
        private String facfpg;
        private String facopg;
        private String factpg;
        private Double facdto;
        private String ternom;
        private String ternif;

        public Builder ent(Integer ent) { this.ent = ent; return this; }
        public Builder eje(String eje) { this.eje = eje; return this; }
        public Builder facnum(Integer facnum) { this.facnum = facnum; return this; }
        public Builder tercod(Integer tercod) { this.tercod = tercod; return this; }
        public Builder cgecod(String cgecod) { this.cgecod = cgecod; return this; }
        public Builder facobs(String facobs) { this.facobs = facobs; return this; }
        public Builder facimp(Double facimp) { this.facimp = facimp; return this; }
        public Builder faciec(Double faciec) { this.faciec = faciec; return this; }
        public Builder facidi(Double facidi) { this.facidi = facidi; return this; }
        public Builder factdc(String factdc) { this.factdc = factdc; return this; }
        public Builder facann(String facann) { this.facann = facann; return this; }
        public Builder facfac(String facfac) { this.facfac = facfac; return this; }
        public Builder facdoc(String facdoc) { this.facdoc = facdoc; return this; }
        public Builder facdat(LocalDateTime facdat) { this.facdat = facdat; return this; }
        public Builder facfco(LocalDateTime facfco) { this.facfco = facfco; return this; }
        public Builder facado(String facado) { this.facado = facado; return this; }
        public Builder factxt(String factxt) { this.factxt = factxt; return this; }
        public Builder facfre(LocalDateTime facfre) { this.facfre = facfre; return this; }
        public Builder conctp(String conctp) { this.conctp = conctp; return this; }
        public Builder concpr(String concpr) { this.concpr = concpr; return this; }
        public Builder conccr(String conccr) { this.conccr = conccr; return this; }
        public Builder facoct(Integer facoct) { this.facoct = facoct; return this; }
        public Builder facfpg(String facfpg) { this.facfpg = facfpg; return this; }
        public Builder facopg(String facopg) { this.facopg = facopg; return this; }
        public Builder factpg(String factpg) { this.factpg = factpg; return this; }
        public Builder facdto(Double facdto) { this.facdto = facdto; return this; }
        public Builder ternom(String ternom) { this.ternom = ternom; return this; }
        public Builder ternif(String ternif) { this.ternif = ternif; return this; }

        public FacWithTerDto build() {
            return new FacWithTerDto(this);
        }
    }

    public Integer getEnt() { return ent; }
    public String getEje() { return eje; }
    public Integer getFacnum() { return facnum; }
    public Integer getTercod() { return tercod; }
    public String getCgecod() { return cgecod; }
    public String getFacobs() { return facobs; }
    public Double getFacimp() { return facimp; }
    public Double getFaciec() { return faciec; }
    public Double getFacidi() { return facidi; }
    public String getFactdc() { return factdc; }
    public String getFacann() { return facann; }
    public String getFacfac() { return facfac; }
    public String getFacdoc() { return facdoc; }
    public LocalDateTime getFacdat() { return facdat; }
    public LocalDateTime getFacfco() { return facfco; }
    public String getFacado() { return facado; }
    public String getFactxt() { return factxt; }
    public LocalDateTime getFacfre() { return facfre; }
    public String getConctp() { return conctp; }
    public String getConcpr() { return concpr; }
    public String getConccr() { return conccr; }
    public Integer getFacoct() { return facoct; }
    public String getFacfpg() { return facfpg; }
    public String getFacopg() { return facopg; }
    public String getFactpg() { return factpg; }
    public Double getFacdto() { return facdto; }
    public String getTernom() { return ternom; }
    public String getTernif() { return ternif; }

    public void setEnt(Integer ent) { this.ent = ent; }
    public void setEje(String eje) { this.eje = eje; }
    public void setFacnum(Integer facnum) { this.facnum = facnum; }
    public void setTercod(Integer tercod) { this.tercod = tercod; }
    public void setCgecod(String cgecod) { this.cgecod = cgecod; }
    public void setFacobs(String facobs) { this.facobs = facobs; }
    public void setFacimp(Double facimp) { this.facimp = facimp; }
    public void setFaciec(Double faciec) { this.faciec = faciec; }
    public void setFacidi(Double facidi) { this.facidi = facidi; }
    public void setFactdc(String factdc) { this.factdc = factdc; }
    public void setFacann(String facann) { this.facann = facann; }
    public void setFacfac(String facfac) { this.facfac = facfac; }
    public void setFacdoc(String facdoc) { this.facdoc = facdoc; }
    public void setFacdat(LocalDateTime facdat) { this.facdat = facdat; }
    public void setFacfco(LocalDateTime facfco) { this.facfco = facfco; }
    public void setFacado(String facado) { this.facado = facado; }
    public void setFactxt(String factxt) { this.factxt = factxt; }
    public void setFacfre(LocalDateTime facfre) { this.facfre = facfre; }
    public void setConctp(String conctp) { this.conctp = conctp; }
    public void setConcpr(String concpr) { this.concpr = concpr; }
    public void setConccr(String conccr) { this.conccr = conccr; }
    public void setFacoct(Integer facoct) { this.facoct = facoct; }
    public void setFacfpg(String facfpg) { this.facfpg = facfpg; }
    public void setFacopg(String facopg) { this.facopg = facopg; }
    public void setFactpg(String factpg) { this.factpg = factpg; }
    public void setFacdto(Double facdto) { this.facdto = facdto; }
    public void setTernom(String ternom) { this.ternom = ternom; }
    public void setTernif(String ternif) { this.ternif = ternif; }
}