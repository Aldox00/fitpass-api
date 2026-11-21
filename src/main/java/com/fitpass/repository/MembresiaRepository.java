package com.fitpass.repository;

import com.fitpass.entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    Optional<Membresia> findFirstByUsuarioIdAndActivaTrueAndFechaFinGreaterThanEqualOrderByIdDesc(Long usuarioId, LocalDate fecha);

}