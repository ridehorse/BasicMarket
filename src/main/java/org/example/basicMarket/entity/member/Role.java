package org.example.basicMarket.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    //RoleType이라는 열거형을 가진 필드를 데이터베이스에 매핑하는데,
    // 해당 필드는 문자열로 저장되어야 하며,
    // NOT NULL이어야 하고 중복되지 않아야 합니다.
    // 예를 들어, RoleType이라는 열거형이 USER, ADMIN 두 가지 상수를 가진다면,
    // 해당 상수들이 문자열로 데이터베이스에 저장됩니다.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,unique = true)
    private RoleType roleType;

    public Role(RoleType roleType){
        this.roleType = roleType;
    }
}
