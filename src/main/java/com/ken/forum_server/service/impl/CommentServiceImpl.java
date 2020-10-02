package com.ken.forum_server.service.impl;

import com.ken.forum_server.dao.CommentDao;
import com.ken.forum_server.dao.PostDao;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.Comment;
import com.ken.forum_server.service.CommentService;
import com.ken.forum_server.util.ConstantUtil;
import com.ken.forum_server.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private PostDao postDao;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentDao.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentDao.selectCountByEntity(entityType,entityId);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        if (comment == null){
            throw new CustomException(CustomExceptionCode.COMMENT_ERROR);
        }

        int rows = commentDao.insertComment(comment);

        if (comment.getEntityType() == ConstantUtil.ENTITY_TYPE_POST){
            int count = commentDao.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            postDao.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }

    @Override
    public Comment findCommentById(int entityId) {
        return commentDao.findCommentById(entityId);
    }

}
