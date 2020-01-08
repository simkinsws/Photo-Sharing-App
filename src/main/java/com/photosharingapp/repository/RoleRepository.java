package com.photosharingapp.repository;

import com.photosharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Role findRoleByName(String name);
}
