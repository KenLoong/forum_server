package com.ken.forum_server.dto;

import lombok.Data;

@Data
public class ChatDto {
    private int fromId;
    private int toId;
    private String content;
}
