package com.ken.forum_server.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ken.forum_server.async.EventHandler;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dao.PostDao;
import com.ken.forum_server.dao.UserDao;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.LikeService;
import com.ken.forum_server.service.PostService;
import com.ken.forum_server.util.RedisKeyUtil;
import com.ken.forum_server.vo.PaginationVo;
import com.ken.forum_server.vo.PostVo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ken.forum_server.util.ConstantUtil.ENTITY_TYPE_POST;
import static com.ken.forum_server.util.ConstantUtil.TOPIC_PUBLISH;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostDao postDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private LikeService likeService;
//    @Autowired
//    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<Post>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;


    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<Post>>() {
                    @Nullable
                    @Override
                    public List<Post> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(key);
                        // 二级缓存: Redis -> mysql

                        logger.info("load post list from DB.");
                        return postDao.listByHot(offset);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.info("load post rows from DB.");
                        return postDao.count();
                    }
                });
    }


    @Override
    public Result publish(Post post) {
        //默认都是普通文章
        post.setType(0);
//        post.setCreateTime(LocalDateTime.now());
        post.setCreateTime(new Date());
        post.setCommentCount(0);
        post.setScore(0);

        //处理转义字符
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        postDao.addPost(post);


        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(post.getUserId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        //用kafka
//        eventProducer.fireEvent(event);
        EventHandler.handleTask(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        return new Result().success("发表成功",post.getId());
    }

    @Override
    public PaginationVo<PostVo> list(int currentPage , int listMode) {
        int offset = (currentPage-1)*5;
        List<Post> posts;
        int total = 0;

        if (listMode == 0){
            logger.info("loading posts form DB");
            posts = postDao.list(offset);
            total = postDao.count();
        }else {
            posts = postListCache.get(offset+"");
            total = postRowsCache.get(0);
//            posts = postDao.listByHot(offset);
        }
        List<PostVo> postVos = new ArrayList<>();
        for (Post post : posts) {
            PostVo postVo = new PostVo();
            //查询文章点赞数量
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            post.setLikeCount(likeCount);
            //转化日期格式
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            post.setCreateTimeStr(sdf.format(post.getCreateTime()));

            //转义
            String content = HtmlUtils.htmlUnescape(post.getContent());
            String title = HtmlUtils.htmlUnescape(post.getTitle());
            post.setTitle(title);
            post.setContent(content);
            //简短内容

            if (content.length() > 10){
                post.setContent(post.getContent().substring(0,10)+"...");
            }


            postVo.setPost(post);
            User user = userDao.findUserById(post.getUserId());
            postVo.setUser(user);
            postVos.add(postVo);
        }
        PaginationVo paginationVo = new PaginationVo<PostVo>();
        paginationVo.setCurrentPage(currentPage);

        paginationVo.setTotal(total);
        paginationVo.setPageSize(5);
        paginationVo.setRecords(postVos);
        return paginationVo;
    }

    @Override
    public Post findPostById(int pid) {
        //转义
        Post post = postDao.findById(pid);
        post.setContent(HtmlUtils.htmlUnescape(post.getContent()));
        post.setTitle(HtmlUtils.htmlUnescape(post.getTitle()));
        return post;
    }

    /**
     * 更新帖子评论数量
     * @param id
     * @param commentCount
     */
    @Override
    public void updateCommentCount(int id, int commentCount) {
        postDao.updateCommentCount(id,commentCount);
    }


    @Override
    public int updateType(int id, int type) {
        return postDao.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return postDao.updateStatus(id, status);
    }

    @Override
    public int updateScore(int postId, double score) {
        return postDao.updateScore(postId, score);
    }

    @Override
    public PaginationVo<PostVo> listByUserId(int currentPage, int uid) {
        int offset = (currentPage-1)*5;
        List<Post> posts = postDao.selectPosts(uid,offset,5);
        int total = postDao.selectUserPostsCount(uid);

        List<PostVo> postVos = new ArrayList<>();
        for (Post post : posts) {
            PostVo postVo = new PostVo();
            //查询文章点赞数量
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            post.setLikeCount(likeCount);
            //转化日期格式
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            post.setCreateTimeStr(sdf.format(post.getCreateTime()));

            post.setContent(HtmlUtils.htmlUnescape(post.getContent()));
            post.setTitle(HtmlUtils.htmlUnescape(post.getTitle()));

            postVo.setPost(post);
            postVos.add(postVo);
        }
        PaginationVo paginationVo = new PaginationVo<PostVo>();
        paginationVo.setCurrentPage(currentPage);

        paginationVo.setTotal(total);
        paginationVo.setPageSize(5);
        paginationVo.setRecords(postVos);
        return paginationVo;
    }

    @Override
    public void deteteById(int entityId) {
        postDao.deteteById(entityId);
    }
}
