package org.example.basicMarket.config.security.gaurd;

import org.example.basicMarket.entity.member.RoleType;

import java.util.List;

public abstract class Guard {

    // 매소드를 재정의 할수 없도록 final로 지정
    public final boolean check(Long id){
        return AuthHelper.isAuthenticated() && (hasRole(getRoleTypes()) || isResourceOwner(id));

        abstract protected List<RoleType> getRoleTypes();
        abstract protected boolean isResourceOwner(Long id);

        private boolean hasRole(List<RoleType> roleTypes) {
            return roleTypes.stream().allMatch(roleType -> AuthHelper.extractMemberRoles().contains(roleType));
        }
    }
}
