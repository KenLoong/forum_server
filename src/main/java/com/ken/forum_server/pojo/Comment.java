package com.ken.forum_server.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Comment implements Serializable {

    private int id;
    private int userId;
    private int entityType; //
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private long likeCount;
    private int likeStatus;

    private int postId;//冗余属性，方便通知而已


}
