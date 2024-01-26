package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.post.Image;
import org.example.basicMarket.entity.post.Post;

import java.util.List;

import static org.example.basicMarket.factory.entity.CategoryFactory.createCategory;
import static org.example.basicMarket.factory.entity.MemberFactory.createMember;

public class PostFactory {

    public static Post createPost() {
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category) {
        return new Post("title", "content", 1000L, member, category, List.of());
    }

    public static Post createPostWithImages(Member member, Category category, List<Image> images) {
        return new Post("title", "content", 1000L, member, category, images);
    }

    public static Post createPostWithImages(List<Image> images) {
        return new Post("title", "content", 1000L, createMember(), createCategory(), images);
    }
}
