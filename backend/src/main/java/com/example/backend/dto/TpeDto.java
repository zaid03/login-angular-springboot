package com.example.backend.dto;

public class TpeDto {

    private Integer tercod;
    private String tpenom;
    private String tpetel;
    private String tpetmo;
    private String tpecoe;
    private String tpeobs;

    public TpeDto(Integer tercod, String tpenom, String tpetel, String tpetmo, String tpecoe, String tpeobs) {
        this.tercod = tercod;
        this.tpenom = tpenom;
        this.tpetel = tpetel;
        this.tpetmo = tpetmo;
        this.tpecoe = tpecoe;
        this.tpeobs = tpeobs;
    }

    public Integer gettercod() { return tercod; }
    public void settercod(Integer tercod) { this.tercod = tercod; }

    public String gettpenom() { return tpenom; }
    public void settpenom(String tpenom) { this.tpenom = tpenom; }

    public String gettpetel() { return tpetel; }
    public void settpetel(String tpetel) { this.tpetel = tpetel; }

    public String gettpetmo() { return tpetmo; }
    public void settpetmo(String tpetmo) { this.tpetmo = tpetmo; }

    public String gettpecoe() { return tpecoe; }
    public void settpecoe(String tpecoe) { this.tpecoe = tpecoe; }

    public String gettpeobs() { return tpeobs; }
    public void settpeobs(String tpeobs) { this.tpeobs = tpeobs; }
}