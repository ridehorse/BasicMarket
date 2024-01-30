package org.example.basicMarket.factory.dto;

import org.example.basicMarket.dto.comment.CommentReadCondition;

public class CommentReadConditionFactory {

    public static CommentReadCondition createCommentReadCondition() {
        return new CommentReadCondition(1L);
    }

    public static CommentReadCondition createCommentReadCondition(Long postId) {
        return new CommentReadCondition(postId);
    }
}
