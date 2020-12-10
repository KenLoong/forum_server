package com.ken.forum_server.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDto {
    private String fromId;
    private String toId;
    private String content;
    private Date createTime;
}
