package com.fitpass.repository;

import com.fitpass.entity.Pase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaseRepository extends JpaRepository<Pase, Long> {

    List<Pase> findByUsuarioIdOrderByFechaCheckInDesc(Long usuarioId);
    List<Pase> findByGimnasioIdOrderByFechaCheckInDesc(Long gimnasioId);
    List<Pase> findByGimnasioIdAndFechaCheckInBetween(Long gimnasioId, LocalDateTime inicio, LocalDateTime fin);

    Optional<Pase> findFirstByUsuarioIdAndFechaCheckOutIsNullOrderByFechaCheckInDesc(Long usuarioId);
}