package com.ken.forum_server.controller;

import com.ken.forum_server.common.Result;
import com.ken.forum_server.event.EventProducer;
import com.ken.forum_server.pojo.Comment;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.service.CommentService;
import com.ken.forum_server.service.PostService;
import com.ken.forum_server.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.ken.forum_server.util.ConstantUtil.*;

@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private PostService postService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    public Result comment(@RequestBody Comment comment){
        int userId = getUserId(request);
        comment.setUserId(userId);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(userId)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", comment.getPostId());
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            Post target = postService.findPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        //如果评论了帖子，则需要更新es库中评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(comment.getUserId());
            eventProducer.fireEvent(event);

            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, comment.getPostId());

        }

        return new Result().success("评论成功");
    }


}
