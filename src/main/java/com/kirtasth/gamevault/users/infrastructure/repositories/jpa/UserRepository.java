package com.kirtasth.gamevault.users.infrastructure.repositories.jpa;

import com.kirtasth.gamevault.users.infrastructure.dtos.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("""
            UPDATE UserEntity u
            SET u.accountLocked = true, u.lockReason = :reason, u.lockInstant = CURRENT_TIMESTAMP
            WHERE u.id = :id
            """)
    Boolean lockUserById(@Param("id") Long id, @Param("reason") String reason);


    @Modifying
    @Transactional
    @Query("""
            UPDATE UserEntity u
            SET u.accountLocked = false, u.lockReason = null, u.lockInstant = null
            WHERE u.id = :id
            """)
    Boolean unlockUserById(@Param("id") Long id);


    @Modifying
    @Transactional
    @Query("""
            UPDATE UserEntity u
            SET u.deletedAt = CURRENT_TIMESTAMP
            WHERE u.id = :id
            """)
    Boolean softDeleteUserById(@Param("id") Long id);
}
