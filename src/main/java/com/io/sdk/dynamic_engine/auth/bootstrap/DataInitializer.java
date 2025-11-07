package com.io.sdk.dynamic_engine.auth.bootstrap;

import com.io.sdk.dynamic_engine.auth.entity.Permission;
import com.io.sdk.dynamic_engine.auth.entity.Role;
import com.io.sdk.dynamic_engine.auth.entity.User;
import com.io.sdk.dynamic_engine.auth.repository.PermissionRepository;
import com.io.sdk.dynamic_engine.auth.repository.RoleRepository;
import com.io.sdk.dynamic_engine.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ✅ List of system permissions
        Set<String> defaultPermissions = Set.of(
                "USER_CREATE",
                "USER_UPDATE",
                "USER_DELETE",

                "ROLE_CREATE",
                "ROLE_UPDATE",
                "ROLE_DELETE",

                "PERMISSION_MANAGE",
                "SYSTEM_SETTINGS");

        // ✅ Insert permissions (if missing)
        defaultPermissions.forEach(name -> {
            permissionRepo.findByName(name)
                    .orElseGet(() -> permissionRepo.save(new Permission(null, name)));
        });

        // ✅ Create SUPER_ADMIN role
        Role superAdminRole = roleRepo.findByName("SUPER_ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setName("SUPER_ADMIN");
            return roleRepo.save(r);
        });

        // ✅ Always give SUPER_ADMIN all permissions (dynamic)
        Set<Permission> allPermissions = permissionRepo.findAll()
                .stream().collect(Collectors.toSet());

        superAdminRole.setPermissions(allPermissions);
        roleRepo.save(superAdminRole); // ← update permissions

        // ✅ Create SUPER_ADMIN user if not exists
        userRepo.findByEmail("superadmin@system.com")
                .orElseGet(() -> {
                    User admin = new User();
                    admin.setEmail("superadmin@system.com");
                    admin.setPassword(passwordEncoder.encode("ChangeThis123!"));
                    admin.setActive(true);
                    admin.setRoles(Set.of(superAdminRole));
                    return userRepo.save(admin);
                });

        System.out.println("✅ SUPER_ADMIN is ready.");
    }
}
