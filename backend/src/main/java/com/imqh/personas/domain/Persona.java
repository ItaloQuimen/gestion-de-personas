package com.imqh.personas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solicitud_id", nullable = false, unique = true, updatable = false, length = 36)
    private String solicitudId;

    @NotBlank
    private String rut;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @Embedded
    @NotNull
    private Direccion direccion;

    protected Persona() {
    }

    public Persona(
            String solicitudId,
            String rut,
            String nombre,
            String apellido,
            LocalDate fechaNacimiento,
            Direccion direccion) {
        this.solicitudId = solicitudId;
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
    }

    public Long getId() {
        return id;
    }

    public String getSolicitudId() {
        return solicitudId;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}
