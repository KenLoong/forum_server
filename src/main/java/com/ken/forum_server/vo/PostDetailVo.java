package com.ken.forum_server.vo;

import lombok.Data;
import java.util.List;

@Data
public class PostDetailVo {
    PostVo postVo;
    List<CommentVo> comments;
    List<String> pictures;
    private int likeStatus;
    private int collectStatus;
}
