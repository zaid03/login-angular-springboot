package com.example.backend.sqlserver1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rpm")
public class Rpm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PERCOD", nullable = false)
    private String PERCOD;
    @Column(name = "APLCOD", nullable = false)
    private int APLCOD;
    @Column(name = "MNUCOD", nullable = false)
    private String MNUCOD;

    public Rpm() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPERCOD() {
        return PERCOD;
    }
    public void setPERCOD(String PERCOD) {
        this.PERCOD = PERCOD;
    }

    public int getAPLCOD() {
        return APLCOD;
    }
    public void setAPLCOD(int APLCOD) {
        this.APLCOD = APLCOD;
    }

    public String getMNUCOD() {
        return MNUCOD;
    }
    public void setMNUCOD(String MNUCOD) {
        this.MNUCOD = MNUCOD;
    }
}
