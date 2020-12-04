package com.ken.forum_server.service;

import com.ken.forum_server.pojo.Comment;

import java.util.List;

public interface CommentService {

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    public int findCommentCount(int entityType, int entityId) ;

    public int addComment(Comment comment);

    Comment findCommentById(int entityId);

    String findCommentByIdAndType(int entityType, int entityId);
}
