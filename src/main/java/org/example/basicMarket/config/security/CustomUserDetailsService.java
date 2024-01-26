package org.example.basicMarket.config.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.repository.member.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException{
        Member member = memberRepository.findWithRolesById(Long.valueOf(userId)) // String을 Long으로 변환
                .orElseGet(()->new Member(null,null,null,null, List.of())); // 사용자를 찾지 못했다면 권한이 없고 비어있는 CustomUserDetails를 생성하여 반환

        // 권한 등급을 GrantedAuthority interface 타입으로 받는다. 구현체인 SimpleGrantedAuthority를 이용했다.
        return new CustomUserDetails(
                String.valueOf(member.getId()),
                member.getRoles().stream().map(memberRole -> memberRole.getRole())
                        .map(role->role.getRoleType())
                        .map(roleType->roleType.toString()) // string으로 변환하는 이유는 SimpleGrantedAuthority 객체가 role 필드를 String 타입으로 저장한다.
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toSet()) // set<SimpleGrantedAuthority>
        );
    }

}
