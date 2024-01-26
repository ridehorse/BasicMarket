package org.example.basicMarket.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.common.EntityDate;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph(
        name = "Member.roles",
        attributeNodes = @NamedAttributeNode(value = "roles", subgraph = "Member.roles.role")
)
public class Member extends EntityDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false,length = 30,unique = true)
    private String email;

    private String password;

    @Column(nullable = false,length = 20)
    private String username;

    @Column(nullable = false,unique = true,length = 20)
    private String nickname;

    // MemberRole에서 fk인(member_id)로 member의 pk인(id)와 연결중이다. 그러므로 외래키의 주인은 MemberRole객체이다.
    // CascadeType.All : fk가 참조하는 pk의 행에 어떤 작업(ALL)이 일어나면 fk도 같이 작업이 일어난다.
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<MemberRole> roles;

    public Member(String email, String password, String username, String nickname, List<Role> roles) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.roles = roles.stream().map(r -> new MemberRole(this,r)).collect(Collectors.toSet());
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }
}
