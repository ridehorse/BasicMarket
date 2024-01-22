package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.member.Role;

import java.util.Collections;
import java.util.List;

public class MemberFactory {

    public static Member createMember() {
        return new Member("email@email.com", "123456a!", "username", "nickname", List.of());
    }

    public static Member createMemberWithRoles(List<Role> roles) {
        return new Member("email@email.com", "123456a!", "username", "nickname", roles);
    }

    public static Member createMember(String email, String password, String username, String nickname) {
        return new Member(email, password, username, nickname, Collections.emptyList());
    }


}
