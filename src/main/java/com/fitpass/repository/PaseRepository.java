package com.fitpass.repository;

import com.fitpass.entity.Pase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaseRepository extends JpaRepository<Pase, Long> {
    List<Pase> findByUsuarioIdOrderByFechaCheckInDesc(Long usuarioId);
    List<Pase> findByGimnasioIdOrderByFechaCheckInDesc(Long gimnasioId);
}