package com.example.backend.dto;

import java.time.LocalDate;

public record UpdateBolsaRequest(
        double gbsimp,
        double gbsius,
        String gbseco,
        LocalDate gbsfop) {
}
