package com.ken.forum_server.vo;

import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.pojo.User;
import lombok.Data;

@Data
public class MessageVo {
    private Message conversation;
    private int letterCount;
    private int unreadCount;
    private User target;
}
