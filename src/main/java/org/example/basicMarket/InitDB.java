package org.example.basicMarket;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.member.Role;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.exception.RoleNotFoundException;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.member.RoleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local") // "local" 프로파일이 활성화된 경우에만 스프링 컨테이너에 등록되고 초기화코드가 실행된다.
public class InitDB {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private  final PasswordEncoder passwordEncoder;

    // @Transactional 적용 불가능하여 제거
    // @PostConstruct // 빈의 생성과 의존성 주입이 끝난 뒤에 수행할 초기화 코드 지정할수 있다.
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDB() {
        log.info("initialize database");
        initRole(); // 3
        initTestAdmin();
        initTestMember();
    }

    private void initRole() {
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin(){
        memberRepository.save(
                new Member("admin@admin.com",passwordEncoder.encode("123456a!"),"admin","admin",
                       List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                               roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new)))
        );

    }

    private void initTestMember(){
        memberRepository.saveAll(
                List.of(
                        new Member("member1@member.com", passwordEncoder.encode("123456a!"),"member1","member1",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))),
                        new Member("member2@member.com", passwordEncoder.encode("123456a!"), "member2", "member2",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))))
                );
    }

}
