package com.example.backend.sqlserver2.model;

import java.io.Serializable;
import java.util.Objects;

public class AfaId implements Serializable {
    private Integer ENT;
    private String AFACOD;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AfaId afaId = (AfaId) o;
        return Objects.equals(ENT, afaId.ENT) &&
               Objects.equals(AFACOD, afaId.AFACOD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ENT, AFACOD);
    }
}
