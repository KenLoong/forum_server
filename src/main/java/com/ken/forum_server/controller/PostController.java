package com.ken.forum_server.controller;

import com.ken.forum_server.annotation.TokenFree;
import com.ken.forum_server.async.EventHandler;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.CommentDto;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.Comment;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.*;
import com.ken.forum_server.util.ConstantUtil;
import com.ken.forum_server.util.RedisKeyUtil;
import com.ken.forum_server.vo.*;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ken.forum_server.util.ConstantUtil.*;

@RestController
@RequestMapping("/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CollectService collectService;

//    @Autowired
//    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发表推文
     * @param post
     * @return
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody Post post ){
        int userId = getUserId(request);
        post.setUserId(userId);
        return postService.publish(post);
    }

    /**
     *分页展示推文
     * @return
     */
    @TokenFree
    @GetMapping("/list")
    public Result posts(@RequestParam(defaultValue = "1") int currentPage,@RequestParam(defaultValue = "0")int listMode){
        PaginationVo<PostVo> pagination = postService.list(currentPage,listMode);
        return new Result().success("",pagination);
    }


    /**
     * 帖子详情
     * @param pid
     * @return
     */
    @TokenFree
    @GetMapping("/detail/{pid}")
    public Result postDetail(@PathVariable("pid") int pid){
        //帖子
        Post post = postService.findPostById(pid);
        if (post.getStatus() == 2){
            throw new CustomException(CustomExceptionCode.PATH_ERROR);
        }

        //设置点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        post.setLikeCount(likeCount);

        //设置收藏数
        long collectCount = collectService.findEntityCollectCount(pid);
        post.setCollectCount(collectCount);

        //转化日期格式
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        post.setCreateTimeStr(sdf.format(post.getCreateTime()));


        //作者
        User user = userService.findUserById(post.getUserId());

        // 点赞状态
        int likeStatus = !isLogin(request) ? 0 :
                likeService.findEntityLikeStatus(getUserId(request), ENTITY_TYPE_POST, pid);

        // 收藏状态
        int collectStatus = !isLogin(request) ? 0 :
                collectService.findEntityCollectStatus(getUserId(request),pid);

        //帖子图片
        List<String> pictures = pictureService.findByPid(post.getId());

        PostVo postVo = new PostVo();
        postVo.setPost(post);
        postVo.setUser(user);


        //一级评论列表，默认前5条
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), 0, 5);

        //一级评论vo集合
        List<CommentVo> commentVoList = new ArrayList<>();

        if (commentList != null){
            for (Comment comment : commentList) {

                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId());
                comment.setLikeCount(likeCount);
                // 点赞状态
                likeStatus = !isLogin(request) ? 0 :
                        likeService.findEntityLikeStatus(getUserId(request), ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId());

                comment.setLikeStatus(likeStatus);

                CommentVo commentVo = new CommentVo();
                //评论
                commentVo.setComment(comment);
                //作者
                commentVo.setUser(userService.findUserById(comment.getUserId()));

                //回复列表（二级评论）,全查
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                //回复vo集合
                List<ReplyVo> replyVoList = new ArrayList<>();

                if (replyList != null){
                    for (Comment reply : replyList) {
                        ReplyVo replyVo = new ReplyVo();

                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ConstantUtil.ENTITY_TYPE_COMMENT, reply.getId());
                        reply.setLikeCount(likeCount);
                        // 点赞状态
                        likeStatus = !isLogin(request) ? 0 :
                                likeService.findEntityLikeStatus(getUserId(request), ConstantUtil.ENTITY_TYPE_COMMENT, reply.getId());
                        reply.setLikeStatus(likeStatus);
                        replyVo.setReply(reply);
                        replyVo.setUser(userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.setTarget(target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.setReplies(replyVoList);

                //回复(二级评论)数量
                commentVo.setReplyCount(replyVoList.size());
                commentVoList.add(commentVo);
            }
        }
        PostDetailVo postDetailVo = new PostDetailVo();
        postDetailVo.setComments(commentVoList);
        postDetailVo.setPostVo(postVo);
        postDetailVo.setPictures(pictures);
        postDetailVo.setLikeStatus(likeStatus);
        postDetailVo.setCollectStatus(collectStatus);

        return new Result().success("",postDetailVo);
    }


    /**
     * 查询帖子评论
     * @param commentDto
     * @return
     */
    @TokenFree
    @PostMapping("/comment/list")
    public Result CommentList(@RequestBody CommentDto commentDto){

        //一级评论列表
        int offset = (commentDto.getCurrentPage()-1)*5;
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, commentDto.getPid(), offset, 5);

        //一级评论vo集合
        List<CommentVo> commentVoList = new ArrayList<>();
        long likeCount = 0;
        int likeStatus = 0;

        if (commentList != null){
            for (Comment comment : commentList) {
                CommentVo commentVo = new CommentVo();

                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId());
                comment.setLikeCount(likeCount);
                // 点赞状态
                likeStatus = !isLogin(request) ? 0 :
                        likeService.findEntityLikeStatus(getUserId(request), ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId());
                comment.setLikeStatus(likeStatus);


                //评论
                commentVo.setComment(comment);
                //作者
                commentVo.setUser(userService.findUserById(comment.getUserId()));

                //回复列表（二级评论）,全查
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ConstantUtil.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                //回复vo集合
                List<ReplyVo> replyVoList = new ArrayList<>();

                if (replyList != null){
                    for (Comment reply : replyList) {
                        ReplyVo replyVo = new ReplyVo();
                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ConstantUtil.ENTITY_TYPE_COMMENT, reply.getId());
                        reply.setLikeCount(likeCount);
                        // 点赞状态
                        likeStatus = !isLogin(request) ? 0 :
                                likeService.findEntityLikeStatus(getUserId(request), ConstantUtil.ENTITY_TYPE_COMMENT, reply.getId());
                        reply.setLikeStatus(likeStatus);
                        replyVo.setReply(reply);
                        replyVo.setUser(userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.setTarget(target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.setReplies(replyVoList);

                //回复(二级评论)数量
                commentVo.setReplyCount(replyVoList.size());
                commentVoList.add(commentVo);
            }
        }
        PaginationVo<CommentVo> paginationVo = new PaginationVo<>();
        paginationVo.setRecords(commentVoList);
        paginationVo.setTotal(commentService.findCommentCount(ENTITY_TYPE_POST,commentDto.getPid()));
        paginationVo.setCurrentPage(commentDto.getCurrentPage());
        return new Result().success("",paginationVo);

    }




    /**
     *elementUI的上传文件是把每个文件都发一次请求的
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file, UploadData uploadData) throws IOException {

//        String filePath = "src/main/resources/static/img/photo/";
        String filePath = "/usr/share/nginx/html/dist/img/photo/";
        //判断文件夹目录是否存在
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isEmpty(suffix)) {
            return new Result().fail("上传照片格式不对！");
        }

        //为图片名取随机名
        fileName = UUID.randomUUID().toString()+suffix;
        byte[] fileBytes = file.getBytes();
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(fileBytes);
        out.flush();
        out.close();
        //修改数据库数据
        pictureService.addPicture(uploadData.getPid(),"/img/photo/"+fileName);

        return new Result().success("图片上传成功");
    }


    // 置顶
    @RequestMapping(path = "/top", method = RequestMethod.GET)
    @ResponseBody
    @RequiresRoles({"admin"})
    public Result setTop(int id) {
        postService.updateType(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(getUserId(request))
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        //用kafka
//        eventProducer.fireEvent(event);
        EventHandler.handleTask(event);

        return new Result().success("");
    }

    // 加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.GET)
    @ResponseBody
    @RequiresRoles({"admin"})
    public Result setWonderful(int id) {
        postService.updateStatus(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(getUserId(request))
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        //用kafka
//        eventProducer.fireEvent(event);
        EventHandler.handleTask(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return new Result().success("");
    }

    // 删除
    @RequestMapping(path = "/delete", method = RequestMethod.GET)
    @ResponseBody
    @RequiresRoles({"admin"})
    public Result setDelete(int id) {
        postService.updateStatus(id, 2);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(getUserId(request))
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        //用kafka
//        eventProducer.fireEvent(event);

        EventHandler.handleTask(event);
        return new Result().success("");
    }




}
