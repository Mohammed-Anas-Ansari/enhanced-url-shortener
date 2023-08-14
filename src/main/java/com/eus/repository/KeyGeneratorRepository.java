package com.eus.repository;

import com.eus.entity.KeyGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyGeneratorRepository extends JpaRepository<KeyGenerator, Long> {

    Optional<KeyGenerator> findTopByOrderByIdDesc();
}
