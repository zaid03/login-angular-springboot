package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    @JsonProperty("USUCOD")
    private String USUCOD;
    @JsonProperty("USUPASS")
    private String USUPASS;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String USUCOD, String USUPASS) {
        this.USUCOD = USUCOD;
        this.USUPASS = USUPASS;
    }

    // Getters and Setters
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