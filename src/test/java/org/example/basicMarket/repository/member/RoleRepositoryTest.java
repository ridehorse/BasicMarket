package org.example.basicMarket.repository.member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.example.basicMarket.entity.member.Role;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.exception.RoleNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
public class RoleRepositoryTest {
    @Autowired
    RoleRepository roleRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void createAndReadTest() { // 1
        // given
        Role role = createRole();

        // when
        roleRepository.save(role);
        clear();

        // then
        Role foundRole = roleRepository.findById(role.getId()).orElseThrow(RoleNotFoundException::new);
        Assertions.assertThat(foundRole.getId()).isEqualTo(role.getId());
    }

    @Test
    void deleteTest() { // 2
        // given
        Role role = roleRepository.save(createRole());
        clear();

        // when
        roleRepository.delete(role);

        // then
        Assertions.assertThatThrownBy(() -> roleRepository.findById(role.getId()).orElseThrow(RoleNotFoundException::new))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void uniqueRoleTypeTest() { // 3
        // given
        roleRepository.save(createRole());
        clear();


        // when, then
        Assertions.assertThatThrownBy(() -> roleRepository.save(createRole())) // 대입해야할 매개변수가 없어서 () 로 표시한거구나
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private Role createRole() {
        return new Role(RoleType.ROLE_NORMAL);
    }

    private void clear() {
        em.flush();
        em.clear();
    }
}

