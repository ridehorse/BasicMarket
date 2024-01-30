package org.example.basicMarket;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.entity.comment.Comment;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.member.Role;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.exception.RoleNotFoundException;
import org.example.basicMarket.repository.comment.CommentRepository;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.member.RoleRepository;
import org.example.basicMarket.repository.post.PostRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local") // "local" 프로파일이 활성화된 경우에만 스프링 컨테이너에 등록되고 초기화코드가 실행된다.
public class InitDB {

    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // @Transactional 적용 불가능하여 제거
    // @PostConstruct // 빈의 생성과 의존성 주입이 끝난 뒤에 수행할 초기화 코드 지정할수 있다.
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDB() {
        log.info("initialize database");
        initRole(); // 3
        initTestAdmin();
        initTestMember();
        initCategory();
        initPost();
        initComment();

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

    private void initCategory() {
        Category c1 = categoryRepository.save(new Category("category1", null));
        Category c2 = categoryRepository.save(new Category("category2", c1));
        Category c3 = categoryRepository.save(new Category("category3", c1));
        Category c4 = categoryRepository.save(new Category("category4", c2));
        Category c5 = categoryRepository.save(new Category("category5", c2));
        Category c6 = categoryRepository.save(new Category("category6", c4));
        Category c7 = categoryRepository.save(new Category("category7", c3));
        Category c8 = categoryRepository.save(new Category("category8", null));
    }

    // forEach() : 객체 결과물이 메서드의 결과로 반환안된다. mapToObject() : 객체 결과물이 반환된다.
    // 이 코드에서도 보면 postRepository.save() 매서드 실행해서, DB에 객체들을 저장했다. 객체 반환은 중요한게 아니다.
    private void initPost() {
        Member member = memberRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);
        IntStream.range(0, 100000)
                .forEach(i -> postRepository.save(
                        new Post("title" + i, "content" + i, Long.valueOf(i), member, category, List.of())
                ));
    }

    private void initComment(){

        Member member = memberRepository.findAll().get(0);
        Post post = postRepository.findAll().get(0);
        Comment c1 = commentRepository.save(new Comment("content", member, post, null));
        Comment c2 = commentRepository.save(new Comment("content", member, post, c1));
        Comment c3 = commentRepository.save(new Comment("content", member, post, c1));
        Comment c4 = commentRepository.save(new Comment("content", member, post, c2));
        Comment c5 = commentRepository.save(new Comment("content", member, post, c2));
        Comment c6 = commentRepository.save(new Comment("content", member, post, c4));
        Comment c7 = commentRepository.save(new Comment("content", member, post, c3));
        Comment c8 = commentRepository.save(new Comment("content", member, post, null));
    }

}
