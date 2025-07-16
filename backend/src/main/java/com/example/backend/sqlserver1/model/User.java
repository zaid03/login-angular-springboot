package com.example.backend.sqlserver1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty("USUCOD")
    private String USUCOD;

    @Column(nullable = false)
    @JsonProperty("USUPASS")
    private String USUPASS;

    // Getters and setters

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

    public String getUSUPASS() {
        return USUPASS;
    }
    public void setUSUPASS(String USUPASS) {
        this.USUPASS = USUPASS;
    }
}
