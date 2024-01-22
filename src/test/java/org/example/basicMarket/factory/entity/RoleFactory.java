package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.member.Role;
import org.example.basicMarket.entity.member.RoleType;

public class RoleFactory {

    public static Role createRole() {
        return new Role(RoleType.ROLE_NORMAL);
    }
}
