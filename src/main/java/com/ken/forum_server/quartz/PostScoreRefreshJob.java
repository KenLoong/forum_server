package com.ken.forum_server.quartz;

import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.service.ElasticSearchService;
import com.ken.forum_server.service.LikeService;
import com.ken.forum_server.service.PostService;
import com.ken.forum_server.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ken.forum_server.util.ConstantUtil.ENTITY_TYPE_POST;

public class PostScoreRefreshJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    // 项目开始时间
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-08-15 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化项目开始时间失败!", e);
        }
    }

    /**
     * 用定时任务来计算帖子分数
     * @param context
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //获取key对应的集合
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            //将集合的元素从集合中移除，然后处理
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    //计算帖子的热度分数
    private void refresh(int postId) {
        Post post = postService.findPostById(postId);

        if (post == null) {
            logger.error("该帖子不存在: id = " + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        // 更新帖子分数（DB）
        postService.updateScore(postId, score);
        // 同步搜索数据（ES）
        post.setScore(score);
        elasticSearchService.savePost(post);
    }

}
