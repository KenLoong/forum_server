package com.ken.forum_server.service;

import com.ken.forum_server.util.RedisKeyUtil;

import java.util.List;
import java.util.Map;

public interface CollectService {

    /**
     * 收藏方法
     * @param userId:收藏人ID
     * @param entityId:实体ID
     * @param entityUserId:被收藏人ID
     */
    void collect(int userId,int entityId, int entityUserId);

    void unCollect(int userId,int entityId, int entityUserId);

    //查询某文章被收藏的数量
    long findEntityCollectCount(int entityId) ;

    //查询用户收藏的文章数量
    long findUserCollectCount(int uid) ;

    // 查询某人对某实体的收藏状态
    int findEntityCollectStatus(int userId, int pid);

    //查询用户收藏的文章
    List<Map<String, Object>> findCollections(int userId, int offset, int limit);


}
