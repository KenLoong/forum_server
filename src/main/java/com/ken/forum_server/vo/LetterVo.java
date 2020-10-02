package com.ken.forum_server.vo;

import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.pojo.User;
import lombok.Data;

@Data
public class LetterVo {
    private Message letter;
    private User fromUser;
}
