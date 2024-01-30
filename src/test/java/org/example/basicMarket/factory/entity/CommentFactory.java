package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.comment.Comment;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.post.Post;

import static org.example.basicMarket.factory.entity.MemberFactory.createMember;
import static org.example.basicMarket.factory.entity.PostFactory.createPost;

public class CommentFactory {

    public static Comment createComment(Comment parent) {
        return new Comment("content", createMember(), createPost(), parent);
    }

    public static Comment createDeletedComment(Comment parent) {
        Comment comment = new Comment("content", createMember(), createPost(), parent);
        comment.delete(); // deteled = true
        return comment;
    }

    public static Comment createComment(Member member, Post post, Comment parent) {
        return new Comment("content", member, post, parent);
    }
}
