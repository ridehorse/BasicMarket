package org.example.basicMarket.repository.member;

import org.example.basicMarket.entity.member.Role;
import org.example.basicMarket.entity.member.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByRoleType(RoleType roleType);

}
