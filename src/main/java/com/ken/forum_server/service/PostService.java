package com.ken.forum_server.service;

import com.ken.forum_server.common.Result;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.vo.PaginationVo;
import com.ken.forum_server.vo.PostVo;

public interface PostService {
    Result publish(Post post);

    PaginationVo<PostVo> list(int currentPage , int listMode);

    Post findPostById(int pid);

    void updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int postId, double score);

    PaginationVo<PostVo> listByUserId(int currentPage, int uid);

    void deteteById(int entityId);

}
