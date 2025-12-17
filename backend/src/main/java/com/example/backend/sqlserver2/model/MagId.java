package com.example.backend.sqlserver2.model;

import java.io.Serializable;
import java.util.Objects;

public class MagId implements Serializable {
    private Integer ENT;
    private Integer MAGCOD;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MagId magId = (MagId) o;
        return Objects.equals(ENT, magId.ENT) &&
               Objects.equals(MAGCOD, magId.MAGCOD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ENT, MAGCOD);
    }
}
