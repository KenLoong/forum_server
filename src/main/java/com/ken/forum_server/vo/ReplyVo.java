package com.ken.forum_server.vo;

import com.ken.forum_server.pojo.Comment;
import com.ken.forum_server.pojo.User;
import lombok.Data;

@Data
public class ReplyVo {
    Comment reply;
    User user;
    User target;
}
