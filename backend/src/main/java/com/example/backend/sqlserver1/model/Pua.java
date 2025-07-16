package com.example.backend.sqlserver1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pua")
public class Pua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUCOD", nullable = false)
    private String USUCOD;
    @Column(name = "APLCOD", nullable = false)
    private int APLCOD;
    @Column(name = "ENTCOD", nullable = false)
    private int ENTCOD;
    @Column(name = "PERCOD", nullable = false)
    private String PERCOD;

    public Pua() {}
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
}
