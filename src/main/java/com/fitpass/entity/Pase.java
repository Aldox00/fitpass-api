package com.fitpass.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pases")
public class Pase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "gimnasio_id", nullable = false)
    private Gimnasio gimnasio;

    @ManyToOne
    @JoinColumn(name = "membresia_id", nullable = false)
    private Membresia membresia;

    @Column(name = "fecha_checkin", nullable = false)
    private LocalDateTime fechaCheckIn;

    @Column(name = "fecha_checkout")
    private LocalDateTime fechaCheckOut;

    @Column(name = "estado", nullable = false)
    private String estado = "ACTIVO";

    public Pase() {}

    public Pase(Usuario usuario, Gimnasio gimnasio, Membresia membresia) {
        this.usuario = usuario;
        this.gimnasio = gimnasio;
        this.membresia = membresia;
        this.fechaCheckIn = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Gimnasio getGimnasio() { return gimnasio; }
    public void setGimnasio(Gimnasio gimnasio) { this.gimnasio = gimnasio; }

    public Membresia getMembresia() { return membresia; }
    public void setMembresia(Membresia membresia) { this.membresia = membresia; }

    public LocalDateTime getFechaCheckIn() { return fechaCheckIn; }
    public void setFechaCheckIn(LocalDateTime fechaCheckIn) { this.fechaCheckIn = fechaCheckIn; }

    public LocalDateTime getFechaCheckOut() { return fechaCheckOut; }
    public void setFechaCheckOut(LocalDateTime fechaCheckOut) { this.fechaCheckOut = fechaCheckOut; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}