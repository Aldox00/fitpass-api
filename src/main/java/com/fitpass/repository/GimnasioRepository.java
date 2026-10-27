package com.fitpass.repository;

import com.fitpass.entity.Gimnasio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GimnasioRepository extends JpaRepository<Gimnasio, Long> {
    List<Gimnasio> findByEsActivoTrue();
    List<Gimnasio> findByCiudadContainingIgnoreCaseAndEsActivoTrue(String ciudad);
}