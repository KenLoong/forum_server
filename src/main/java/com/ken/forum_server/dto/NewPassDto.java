package com.ken.forum_server.dto;

import lombok.Data;

@Data
public class NewPassDto {
    private String oldpass;
    private String pass;
}
