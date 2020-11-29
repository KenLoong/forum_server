package com.ken.forum_server.controller;

import com.ken.forum_server.annotation.TokenFree;
import com.ken.forum_server.async.EventHandler;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.FollowDto;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.FollowService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ken.forum_server.util.ConstantUtil.TOPIC_FOLLOW;

@RestController
public class FollowController extends BaseController{

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;
//    @Autowired
//    private EventProducer eventProducer;

    //关注
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public Result follow(@RequestBody FollowDto followDto) {
        User user = userService.findUserById(getUserId(request));
        int entityId = followDto.getEntityId();
        int entityType = followDto.getEntityType();

        //不可以自己关注自己
        if (user.getId() == entityId){
            return new Result();
        };

        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);

        //往kafka中发送事件
//        eventProducer.fireEvent(event);
        //用线程池去发送事件
        EventHandler.handleTask(event);

        // 返回粉丝数量
        long followerCount = followService.findFollowerCount(ConstantUtil.ENTITY_TYPE_USER, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("followerCount", followerCount);
        map.put("hasFollowed", true);
        return new Result().success(map);
    }

    //取消关注
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public Result unfollow(@RequestBody FollowDto followDto) {
        User user = userService.findUserById(getUserId(request));
        int entityId = followDto.getEntityId();
        int entityType = followDto.getEntityType();

        followService.unfollow(user.getId(), entityType, entityId);

        // 粉丝数量
        long followerCount = followService.findFollowerCount(ConstantUtil.ENTITY_TYPE_USER, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("followerCount", followerCount);
        map.put("hasFollowed", false);
        return new Result().success(map);
    }

    //查询用户关注的人
    @TokenFree
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public Result getFollowees(@PathVariable("userId") int userId, @RequestParam(defaultValue = "1") int currentPage) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);

        long followeeCount = followService.findFolloweeCount(userId, ConstantUtil.ENTITY_TYPE_USER);
        map.put("total",followeeCount);

        int offset = (currentPage-1)*5;
        List<Map<String, Object>> userList = followService.findFollowees(userId, offset,5);

        if (userList != null) {
            for (Map<String, Object> m : userList) {
                User u = (User) m.get("user");
                m.put("hasFollowed", hasFollowed(u.getId()));
            }
        }

        map.put("userList",userList);

        return new Result().success(map);
    }

    //查询用户的粉丝
    @TokenFree
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public Result getFollowers(@PathVariable("userId") int userId, @RequestParam(defaultValue = "1") int currentPage) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new CustomException(CustomExceptionCode.USER_NOT_EXIST);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);


        // 粉丝数量
        long followerCount = followService.findFollowerCount(ConstantUtil.ENTITY_TYPE_USER, userId);
        map.put("total",followerCount);

        int offset = (currentPage-1)*5;
        List<Map<String, Object>> userList = followService.findFollowers(userId, offset,5);
        if (userList != null) {
            for (Map<String, Object> m : userList) {
                User u = (User) m.get("user");
                m.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        map.put("userList",userList);

        return new Result().success(map);
    }

    //查询当前用户是否关注了id为userId的用户
    private boolean hasFollowed(int userId) {
        if (!isLogin(request)) {
            return false;
        }

        return followService.hasFollowed(getUserId(request), ConstantUtil.ENTITY_TYPE_USER, userId);
    }

}
