package com.ken.forum_server.controller;

import com.ken.forum_server.async.EventHandler;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.LikeDto;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.LikeService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.ken.forum_server.util.ConstantUtil.ENTITY_TYPE_POST;
import static com.ken.forum_server.util.ConstantUtil.TOPIC_LIKE;

@RestController
public class LikeController extends BaseController{

    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
//    @Autowired
//    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public Result like(@RequestBody LikeDto likeDto) {
        int userId = getUserId(request);
        User user = userService.findUserById(userId);

        // 点赞
        likeService.like(user.getId(), likeDto.getEntityType(), likeDto.getEntityId(),likeDto.getEntityUserId());

        // 数量
        long likeCount = likeService.findEntityLikeCount(likeDto.getEntityType(), likeDto.getEntityId());
        // 状态:已点赞还是未点赞,1代表已点赞，0代表未点赞
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), likeDto.getEntityType(), likeDto.getEntityId());
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        map.put("entityType", likeDto.getEntityType());

        // 触发点赞事件,发送通知
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(userId)
                    .setEntityType(likeDto.getEntityType())
                    .setEntityId(likeDto.getEntityId())
                    .setEntityUserId(likeDto.getEntityUserId())
                    .setData("postId", likeDto.getPostId());

            //用kafka发送事件
//            eventProducer.fireEvent(event);
            EventHandler.handleTask(event);
        }

        if (likeDto.getEntityType() == ENTITY_TYPE_POST){
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, likeDto.getPostId());
        }

        return new Result().success(map);
    }


}
