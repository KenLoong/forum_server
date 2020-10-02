package com.ken.forum_server.dto;

import lombok.Data;

@Data
public class LikeDto {

    private int entityType;
    private int entityId;
    private int entityUserId;
    //冗余属性
    private int postId;
}
