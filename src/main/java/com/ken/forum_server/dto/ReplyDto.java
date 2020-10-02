package com.ken.forum_server.dto;

import lombok.Data;

@Data
public class ReplyDto {
    private Integer replyId ;
    private Integer replyUid ;
    private String replyContent;
}
