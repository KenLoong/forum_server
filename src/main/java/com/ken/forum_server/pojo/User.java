package com.ken.forum_server.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String avatar;
    private String email;
    private Integer gender; // 0代表男  1代表女
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private String code; //激活码
    private int state;  //状态  0代表未激活   1代表已激活
}
