package com.ken.forum_server.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDto {
    private String from_id;
    private String to_id;
    private String content;
    private String from_avatar;
    private String to_avatar;
    private String from_name;
    private String to_name;
    private Date time;
}
