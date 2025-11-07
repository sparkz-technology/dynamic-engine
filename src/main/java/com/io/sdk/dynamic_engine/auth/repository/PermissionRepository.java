package com.io.sdk.dynamic_engine.auth.repository;

import com.io.sdk.dynamic_engine.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);
}
