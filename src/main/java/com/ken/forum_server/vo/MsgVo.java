package com.ken.forum_server.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ken.forum_server.pojo.User;
import lombok.Data;

import java.util.Date;

@Data
public class MsgVo {
    private User fromUser;  //消息是来自谁
    private int msgType;    //消息针对的实体类型  1-帖子 2-评论
    private int entityId;   //消息针对的实体ID
    private String content; //消息具体的内容
    private int postId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
