package com.capricon.User_Service.repository.jpa;

import com.capricon.User_Service.model.Role;
import com.capricon.User_Service.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleType(RoleType roleType);

}
