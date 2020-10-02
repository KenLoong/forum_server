package com.ken.forum_server.dto;

import lombok.Data;

@Data
public class CollectDto {

    private int entityId;//被收藏的文章ID
    private int entityUserId; //被收藏的文章作者ID

}
