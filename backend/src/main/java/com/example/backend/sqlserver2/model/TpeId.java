package com.example.backend.sqlserver2.model;

import java.io.Serializable;
import java.util.Objects;

public class TpeId implements Serializable {
    private Integer ent;
    private Integer tercod;
    private Integer tpecod;

    public Integer getent() { return ent; }
    public void setent(Integer ent) { this.ent = ent; }
    public Integer gettercod() { return tercod; }
    public void settercod(Integer tercod) { this.tercod = tercod; }
    public Integer gettpecod() { return tpecod; }
    public void settpecod(Integer tpecod) { this.tpecod = tpecod; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TpeId)) return false;
        TpeId tpeId = (TpeId) o;
        return Objects.equals(ent, tpeId.ent) &&
               Objects.equals(tercod, tpeId.tercod) &&
               Objects.equals(tpecod, tpeId.tpecod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ent, tercod, tpecod);
    }
}
