package com.ken.forum_server.service.impl;

import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.CollectService;
import com.ken.forum_server.service.PostService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @Override
    public void collect(int userId, int entityId, int entityUserId) {
        //开启事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String postCollectKey = RedisKeyUtil.getPostCollectKey(entityId);
                String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);

                operations.multi();

                //放入有序集合
                operations.opsForZSet().add(userCollectKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(postCollectKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });

    }

    @Override
    public void unCollect(int userId, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String postCollectKey = RedisKeyUtil.getPostCollectKey(entityId);
                String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);

                operations.multi();

                operations.opsForZSet().remove(userCollectKey, entityId);
                operations.opsForZSet().remove(postCollectKey, userId);

                return operations.exec();
            }
        });
    }

    // 查询某文章被收藏的数量
    @Override
    public long findEntityCollectCount(int pid) {
        String postCollectKey = RedisKeyUtil.getPostCollectKey(pid);
        return redisTemplate.opsForZSet().zCard(postCollectKey);
    }

    @Override
    public long findUserCollectCount(int uid) {
        String userCollectKey = RedisKeyUtil.getUserCollectKey(uid);
        return redisTemplate.opsForZSet().zCard(userCollectKey);
    }

    // 查询某人对某实体的收藏状态,返回 1说明已收藏，0说明未收藏
    @Override
    public int findEntityCollectStatus(int userId, int pid) {
        String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);

        return redisTemplate.opsForZSet().score(userCollectKey,pid) == null ? 0 : 1;
    }

    //查询用户收藏的文章
    @Override
    public List<Map<String, Object>> findCollections(int userId, int offset, int limit) {

        String userCollectKey = RedisKeyUtil.getUserCollectKey(userId);
        //获取文章ID
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(userCollectKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            Post post = postService.findPostById(targetId);
            map.put("post",post);

            Double score = redisTemplate.opsForZSet().score(userCollectKey, targetId);

            //转化日期格式
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

            Date date = new Date(score.longValue());
            map.put("collectTime",sdf.format(date));
            list.add(map);
        }

        return list;
    }
}
