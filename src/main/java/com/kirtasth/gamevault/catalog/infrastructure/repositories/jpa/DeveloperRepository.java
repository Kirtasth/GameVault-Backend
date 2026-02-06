package com.kirtasth.gamevault.catalog.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.catalog.infrastructure.dtos.entities.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<DeveloperEntity, Long> {

}
