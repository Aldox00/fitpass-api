package com.fitpass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "pases")
public class Pase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gimnasio_id", nullable = false)
    private Gimnasio gimnasio;

    @Column(name = "fecha_checkin", nullable = false, updatable = false)
    private LocalDateTime fechaCheckIn;

    @Column(nullable = false)
    private String estado; // "VALIDO", "CANCELADO"

    @PrePersist
    protected void onCreate() {
        this.fechaCheckIn = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "VALIDO";
        }
    }

    public Pase() {}

    public Pase(Usuario usuario, Gimnasio gimnasio) {
        this.usuario = usuario;
        this.gimnasio = gimnasio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Gimnasio getGimnasio() { return gimnasio; }
    public void setGimnasio(Gimnasio gimnasio) { this.gimnasio = gimnasio; }

    public LocalDateTime getFechaCheckIn() { return fechaCheckIn; }
    public void setFechaCheckIn(LocalDateTime fechaCheckIn) { this.fechaCheckIn = fechaCheckIn; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}