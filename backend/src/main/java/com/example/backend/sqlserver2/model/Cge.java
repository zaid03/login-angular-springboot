package com.example.backend.sqlserver2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;

@Entity
@IdClass(CgeId.class)
@Table(name = "CGE", schema = "dbo")
public class Cge {

    @Id
    @Column(nullable = false)
    private Integer ENT;

    @Id
    @Column(nullable = false)
    private String EJE;

    @Id
    @Column(nullable = false)
    private String CGECOD;

    @Column(nullable = false)
    private String CGEDES;

    @Column(nullable = true)
    private Integer CGECIC;

    public Integer getENT() { return ENT; }
    public void setENT(Integer ENT) { this.ENT = ENT; }

    public String getEJE() { return EJE; }
    public void setEJE(String EJE){ this.EJE = EJE; }

    public String getCGECOD(){ return CGECOD; }
    public void setCGECOD(String CGECOD) { this.CGECOD = CGECOD; }

    public String getCGEDES() { return CGEDES; }
    public void setCGEDES(String CGEDES) { this.CGEDES = CGEDES; }

    public Integer getCGECIC() { return CGECIC; }
    public void setCGECIC() { this.CGECIC = CGECIC; }
}
