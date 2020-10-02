package com.ken.forum_server.controller;

import com.ken.forum_server.annotation.TokenFree;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.CollectDto;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.CollectService;
import com.ken.forum_server.service.LikeService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CollectController extends BaseController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private CollectService collectService;

    /**
     * 收藏
     * @param collectDto
     * @return
     */
    @PostMapping("/collect")
    public Result collect(@RequestBody CollectDto collectDto){

        int userId = getUserId(request);
        collectService.collect(userId,collectDto.getEntityId(),collectDto.getEntityUserId());
        
        // 返回文章被收藏数量
        long collectCount = collectService.findEntityCollectCount(collectDto.getEntityId());
        Map<String,Object> map = new HashMap<>();
        map.put("collectCount", collectCount);
        map.put("collectStatus", 1);
        return new Result().success(map);
    }

    @PostMapping("/uncollect")
    public Result uncollect(@RequestBody CollectDto collectDto){

        int userId = getUserId(request);
        collectService.unCollect(userId,collectDto.getEntityId(),collectDto.getEntityUserId());

        // 返回粉丝数量
        long collectCount = collectService.findEntityCollectCount(collectDto.getEntityId());
        Map<String,Object> map = new HashMap<>();
        map.put("collectCount", collectCount);
        map.put("collectStatus", 0);
        return new Result().success(map);

    }

    /**
     * 查询用户收藏的文章
     * @param uid
     * @return
     */
    @TokenFree
    @GetMapping("/collection/{uid}")
    public Result postCollection(@PathVariable("uid") int uid,@RequestParam(defaultValue = "1") int currentPage){
        User user = userService.findUserById(uid);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("user",user);

        //查询用户收藏文章总数
        long total = collectService.findUserCollectCount(uid);
        map.put("total",total);

        //查询文章
        int offset = (currentPage-1)*5;
        List<Map<String, Object>> postList = collectService.findCollections(uid, offset,5);
        map.put("posts",postList);
        map.put("currentPage",currentPage);

        return new Result().success(map);
    }
}
