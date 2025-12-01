package com.example.backend.sqlserver2.model;

import java.io.Serializable;
import java.util.Objects;

public class MtaId implements Serializable {
    private Integer ENT;
    private Integer MTACOD;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MtaId mtaId = (MtaId) o;
        return Objects.equals(ENT, mtaId.ENT) &&
               Objects.equals(MTACOD, mtaId.MTACOD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ENT, MTACOD);
    }
}
