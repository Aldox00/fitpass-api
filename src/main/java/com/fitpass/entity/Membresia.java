package com.fitpass.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "membresias")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String tipoPase;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activa = false;

    private Double precio;

    @Column(nullable = false)
    private String estadoPago = "PENDIENTE";

    public Membresia() {}

    public Membresia(Usuario usuario, String tipoPase, LocalDate fechaInicio, LocalDate fechaFin, Boolean activa) {
        this.usuario = usuario;
        this.tipoPase = tipoPase;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
    }

    public Membresia(Usuario usuario, String tipoPase, LocalDate fechaInicio, LocalDate fechaFin, Boolean activa, Double precio, String estadoPago) {
        this.usuario = usuario;
        this.tipoPase = tipoPase;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activa = activa;
        this.precio = precio;
        this.estadoPago = estadoPago;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoPase() { return tipoPase; }
    public void setTipoPase(String tipoPase) { this.tipoPase = tipoPase; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getActiva() { return activa; }
    public Boolean isActiva() { return activa; } // Soporta tanto getActiva() como isActiva()
    public void setActiva(Boolean activa) { this.activa = activa; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}