package com.ken.forum_server.vo;

import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.pojo.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostVo implements Serializable {
    private Post post;
    private User user;
}
