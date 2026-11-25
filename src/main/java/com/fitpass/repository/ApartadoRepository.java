package com.fitpass.repository;

import com.fitpass.entity.Apartado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApartadoRepository extends JpaRepository<Apartado, Long> {

    List<Apartado> findByEstadoOrderByCreadoEnDesc(String estado);

    List<Apartado> findByUsuarioIdAndEstadoOrderByCreadoEnDesc(Long usuarioId, String estado);

    Optional<Apartado> findByUsuarioIdAndProductoIdAndEstado(Long usuarioId, Long productoId, String estado);
}