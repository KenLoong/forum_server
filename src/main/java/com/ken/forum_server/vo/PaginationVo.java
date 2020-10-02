package com.ken.forum_server.vo;

import com.ken.forum_server.pojo.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PaginationVo<T> implements Serializable {

    private List<T> records;
    private int total;
    private int currentPage;
    private int pageSize;
    private int letterUnreadCount;
    private int noticeUnreadCount;
    private User target;


}
