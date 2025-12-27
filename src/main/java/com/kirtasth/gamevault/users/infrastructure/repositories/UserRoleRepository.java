package com.kirtasth.gamevault.users.infrastructure.repositories;

import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserRoleEntity;
import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleKey> {

    List<UserRoleEntity> findAllByUserId(Long userId);
}
