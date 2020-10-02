package com.ken.forum_server.service;

public interface LikeService {

    // 点赞
    /**
     *
     * @param userId:点赞人ID
     * @param entityType：点赞类型：1是帖子，2是评论
     * @param entityId：被点赞实体ID
     * @param entityUserId：被点赞实体所属人ID
     */
    public void like(int userId, int entityType, int entityId , int entityUserId);

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId);

    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId);

    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId) ;

}
