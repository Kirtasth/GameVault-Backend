package com.kirtasth.gamevault.users.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.common.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRole(RoleEnum role);
}
