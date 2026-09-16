package com.imqh.personas.repository;

import com.imqh.personas.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    boolean existsBySolicitudId(String solicitudId);
}
