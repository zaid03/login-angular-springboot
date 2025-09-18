package com.example.backend.sqlserver1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "USU", schema = "dbo")
public class User {

    @Id
    @Column(nullable = false, unique = true)
    @JsonProperty("USUCOD")
    private String USUCOD;

    @Column(nullable = false)
    @JsonProperty("USUPAS")
    private String USUPAS;

    public String getUSUCOD() {
        return USUCOD;
    }
    public void setUSUCOD(String USUCOD) {
        this.USUCOD = USUCOD;
    }

    public String getUSUPAS() {
        return USUPAS;
    }
    public void setUSUPAS(String USUPAS) {
        this.USUPAS = USUPAS;
    }
}
