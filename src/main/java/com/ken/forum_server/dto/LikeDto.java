package com.ken.forum_server.dto;

import lombok.Data;

@Data
public class LikeDto {

    private int entityType;   //被点赞实体的类型  1-文章  2-评论
    private int entityId;      //被点赞实体的ID
    private int entityUserId;   //被点赞人的ID
    //冗余属性
    private int postId;
}
