package org.example.basicMarket.entity.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.entity.common.EntityDate;
import org.example.basicMarket.entity.member.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category; // 2

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Image> images; // 3

    public Post(String title, String content, Long price, Member member, Category category, List<Image> images) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.member = member;
        this.category = category;
        this.images = new ArrayList<>();
        addImages(images); // 4
    }

    private void addImages(List<Image> added) { // 5
        added.stream().forEach(i -> {
            images.add(i);
            i.initPost(this);
        });
    }

}
