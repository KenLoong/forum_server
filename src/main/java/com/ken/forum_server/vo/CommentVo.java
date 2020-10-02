package com.ken.forum_server.vo;

import com.ken.forum_server.pojo.Comment;
import com.ken.forum_server.pojo.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    Comment comment;
    User user;
    List<ReplyVo> replies;
    int replyCount;
}
