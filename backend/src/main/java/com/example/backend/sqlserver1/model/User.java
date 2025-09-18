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
    @JsonProperty("USUPASS")
    private String USUPASS;

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
