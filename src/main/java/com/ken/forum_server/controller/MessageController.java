package com.ken.forum_server.controller;

import com.alibaba.fastjson.JSONObject;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.ChatDto;
import com.ken.forum_server.dto.LetterDto;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.CommentService;
import com.ken.forum_server.service.MessageService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.vo.LetterVo;
import com.ken.forum_server.vo.MessageVo;
import com.ken.forum_server.vo.MsgVo;
import com.ken.forum_server.vo.PaginationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

import static com.ken.forum_server.util.ConstantUtil.*;

@RestController
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    // 会话列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public Result getLetterList(@RequestParam(defaultValue = "1") int currentPage) {


        int userId = getUserId(request);
        User user = userService.findUserById(userId);

        //查询用户有几个会话
        int conversationCount = messageService.findConversationCount(userId);
        messageService.findNoticeCount(userId,"comment");

        int offset = (currentPage - 1)*5;
        //会话列表
        List<Message> conversationList = messageService.findConversations(userId, offset, 5);

        List<MessageVo> conversations = new ArrayList<>();

        if (conversationList != null) {
            for (Message message : conversationList) {
                MessageVo messageVo = new MessageVo();
                messageVo.setConversation(message);
                messageVo.setLetterCount(messageService.findLetterCount(message.getConversationId()));
                if (message.getConversationId() == null){
                    messageVo.setUnreadCount(user.getId());
                }else {
                    messageVo.setUnreadCount(messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                }
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                messageVo.setTarget(userService.findUserById(targetId));
                conversations.add(messageVo);
            }
        }

        // 查询未读私信数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId());
        //查询未读系统消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);

        PaginationVo<MessageVo> paginationVo = new PaginationVo<>();
        paginationVo.setTotal(conversationCount);
        paginationVo.setRecords(conversations);
        paginationVo.setCurrentPage(currentPage);
        paginationVo.setPageSize(5);
        paginationVo.setLetterUnreadCount(letterUnreadCount);
        paginationVo.setNoticeUnreadCount(noticeUnreadCount);

        return new Result<PaginationVo>().success(paginationVo);
    }


    //消息中心
    @RequestMapping(path = "/message/list", method = RequestMethod.GET)
    public Result getNoticeList() {
        User user = userService.findUserById(getUserId(request));

        //
        //查询【回复我的消息总数】（未读）
        int replyCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);

        //查询【点赞我的消息总数】
        int likeCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);

        //查询【新粉丝数量】
        int followCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);

        //查询【系统通知】
        int systemCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_SYSTEM);

        Map<String,Object> map = new HashMap<>();

        map.put("replyCount",replyCount);
        map.put("likeCount",likeCount);
        map.put("followCount",followCount);
        map.put("systemCount",systemCount);



        /*Map<String, Object> commentMap = new HashMap<>();
        if (message != null) {
            commentMap.put("message", message);

            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            commentMap.put("user", userService.findUserById((Integer) data.get("userId")));
            commentMap.put("entityType", data.get("entityType"));
            commentMap.put("entityId", data.get("entityId"));
            commentMap.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            commentMap.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            commentMap.put("unread", unread);
        }
        map.put("commentNotice",commentMap);

        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        Map<String,Object> likeMap = new HashMap<>();
        if (message != null) {
            likeMap.put("message", message);

            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            likeMap.put("user", userService.findUserById((Integer) data.get("userId")));
            likeMap.put("entityType", data.get("entityType"));
            likeMap.put("entityId", data.get("entityId"));
            likeMap.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            likeMap.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            likeMap.put("unread", unread);
        }
        map.put("likeNotice",likeMap);

        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        Map<String,Object> followMap = new HashMap<>();
        if (message != null) {
            followMap.put("message", message);

            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            followMap.put("user", userService.findUserById((Integer) data.get("userId")));
            followMap.put("entityType", data.get("entityType"));
            followMap.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            followMap.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            followMap.put("unread", unread);
        }
        map.put("followNotice",followMap);

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        map.put("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        map.put("noticeUnreadCount", noticeUnreadCount);*/

        return new Result().success(map);
    }


    /**
     * 获取回复我的消息
     * @param currentPage
     * @return
     */
    @RequestMapping("/message/reply")
    public Result getReply(@RequestParam(defaultValue = "1") int currentPage){
        User user = userService.findUserById(getUserId(request));

        int limit = 5;
        int offset = (currentPage-1)*5;

        // 查询评论我的消息(所有的消息，不管是已读还是未读)
        List<Message> messages = messageService.findAllMessage(user.getId(), TOPIC_COMMENT,offset,limit);
        int total = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);

        Map<String, Object> map = new HashMap<>();
        List<MsgVo> msgVos = new ArrayList<>();

        for (Message message : messages) {

            //获取消息的内容
            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            MsgVo msgVo = new MsgVo();
            //评论我的用户
            msgVo.setFromUser(userService.findUserById((Integer) data.get("userId")));

            int entityType = Integer.parseInt("" + data.get("entityType"));
            int entityId = Integer.parseInt("" + data.get("entityId"));
            //评论我的帖子还是评论
            msgVo.setMsgType(entityType);
            //被评论的实体id
            msgVo.setEntityId(entityId);
            msgVo.setContent(""+data.get("content"));
            //该评论所在的帖子
            msgVo.setPostId(Integer.parseInt("" + data.get("postId")));
            msgVo.setCreateTime(message.getCreateTime());


            msgVos.add(msgVo);

        }

        map.put("msgVos",msgVos);
        map.put("total",total);

        //把消息设置为已读
        List<Integer> ids = new ArrayList<>();
        messages.stream().forEach(message -> ids.add(message.getId()));
        //如果ID为空，Sql会有语法错误
        if (ids.size() > 0){
            messageService.readMessage(ids);
        }

        return new Result().success(map);
    }

    /**
     * 获取点赞我的消息
     * @param currentPage
     * @return
     */
    @RequestMapping("/message/like")
    public Result getLike(@RequestParam(defaultValue = "1") int currentPage){
        User user = userService.findUserById(getUserId(request));

        int limit = 5;
        int offset = (currentPage-1)*5;

        // 查询点赞我的消息(所有的消息，不管是已读还是未读)
        List<Message> messages = messageService.findAllMessage(user.getId(), TOPIC_LIKE,offset,limit);
        int total = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);

        Map<String, Object> map = new HashMap<>();
        List<MsgVo> msgVos = new ArrayList<>();

        for (Message message : messages) {

            //获取消息的内容
            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            MsgVo msgVo = new MsgVo();
            //点赞我的用户
            msgVo.setFromUser(userService.findUserById((Integer) data.get("userId")));
            //点赞我的文章还是评论
            int entityType = Integer.parseInt("" + data.get("entityType"));
            msgVo.setMsgType(entityType);
            //该评论所在的帖子
            msgVo.setPostId(Integer.parseInt("" + data.get("postId")));
            msgVo.setCreateTime(message.getCreateTime());
            msgVos.add(msgVo);

        }

        map.put("msgVos",msgVos);
        map.put("total",total);

        //把消息设置为已读
        List<Integer> ids = new ArrayList<>();
        messages.stream().forEach(message -> ids.add(message.getId()));
        //如果ID为空，Sql会有语法错误
        if (ids.size() > 0){
            messageService.readMessage(ids);
        }

        return new Result().success(map);
    }

    /**
     * 获取关注我的消息
     * @param currentPage
     * @return
     */
    @RequestMapping("/message/follow")
    public Result getFollow(@RequestParam(defaultValue = "1") int currentPage){
        User user = userService.findUserById(getUserId(request));

        int limit = 5;
        int offset = (currentPage-1)*5;

        // 查询关注我的消息(所有的消息，不管是已读还是未读)
        List<Message> messages = messageService.findAllMessage(user.getId(), TOPIC_FOLLOW,offset,limit);
        int total = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);

        Map<String, Object> map = new HashMap<>();
        List<MsgVo> msgVos = new ArrayList<>();

        for (Message message : messages) {

            //获取消息的内容
            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            MsgVo msgVo = new MsgVo();
            //关注我的用户
            msgVo.setFromUser(userService.findUserById((Integer) data.get("userId")));
            msgVo.setCreateTime(message.getCreateTime());
            msgVos.add(msgVo);

        }

        map.put("msgVos",msgVos);
        map.put("total",total);

        //把消息设置为已读
        List<Integer> ids = new ArrayList<>();
        messages.stream().forEach(message -> ids.add(message.getId()));
        //如果ID为空，Sql会有语法错误
        if (ids.size() > 0){
            messageService.readMessage(ids);
        }

        return new Result().success(map);
    }

    //获取系统通知
    @RequestMapping("/message/system")
    public Result getSystem(@RequestParam(defaultValue = "1") int currentPage){
        User user = userService.findUserById(getUserId(request));

        int limit = 5;
        int offset = (currentPage-1)*5;

        // 查询系统消息(所有的消息，不管是已读还是未读)
        List<Message> messages = messageService.findAllMessage(user.getId(), TOPIC_SYSTEM,offset,limit);
        int total = messageService.findNoticeCount(user.getId(),TOPIC_SYSTEM);

        Map<String, Object> map = new HashMap<>();
        List<MsgVo> msgVos = new ArrayList<>();

        for (Message message : messages) {

            //获取消息的内容
            String content = message.getContent();
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            MsgVo msgVo = new MsgVo();
            msgVo.setCreateTime(message.getCreateTime());
            msgVos.add(msgVo);

        }

        map.put("msgVos",msgVos);
        map.put("total",total);

        //把消息设置为已读（status置1）
        List<Integer> ids = new ArrayList<>();
        messages.stream().forEach(message -> ids.add(message.getId()));
        //如果ID为空，Sql会有语法错误
        if (ids.size() > 0){
            messageService.readMessage(ids);
        }

        return new Result().success(map);
    }

    //获取与用户有消息来往的用户列表
    @GetMapping("/message/getUsers")
    public Result getUsers(){
        int userId = getUserId(request);
        //连接成功后，查询跟当前用户有消息来往的用户
        List<User> userList = new ArrayList<>();
        Set<Integer> toMeids = messageService.findChatToMeIds(userId);
        Set<Integer> meToIds = messageService.findIChatToids(userId);
        //查询聊天用户列表
        //合并集合
        toMeids.addAll(meToIds);

        List<Integer> ids = new ArrayList<>(toMeids);
        if (toMeids.size() > 0){
            userList = userService.findUserByIds(ids);
        }

        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);

        return new Result().success(map);
    }

    //查找聊天记录，需要对方的id
    @RequestMapping("/message/getSession")
    public Result getSession(@RequestBody ChatDto chatDto){
        int userId = getUserId(request);

        int currentId = chatDto.getToId();

        Map<String,Object> map = new HashMap<>();

        //查询聊天记录
        List<Message> chatList = messageService.findChatList(userId,currentId);
        //消息记录
        map.put("chatList",chatList);

        return new Result().success(map);
    }


    /*//会话详情列表
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public Result getLetterDetail(@PathVariable("conversationId") String conversationId,@RequestParam(defaultValue = "1") int currentPage) {
        int letterCount = messageService.findLetterCount(conversationId);

        int offset = (currentPage-1)*3;

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, offset, 3);
        List<LetterVo> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {

                LetterVo letterVo = new LetterVo();
                letterVo.setLetter(message);
                letterVo.setFromUser(userService.findUserById(message.getFromId()));
                letters.add(letterVo);
            }
        }

        PaginationVo<LetterVo> paginationVo = new PaginationVo<>();
        paginationVo.setPageSize(3);
        paginationVo.setCurrentPage(currentPage);
        paginationVo.setTotal(letterCount);
        //私信目标
        paginationVo.setTarget(getLetterTarget(conversationId));
        paginationVo.setRecords(letters);

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return new Result().success(paginationVo);
    }

    //发送私信
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    public Result sendLetter(@RequestBody LetterDto letterDto) {
        User target = userService.findUserByName(letterDto.getToName());
        if (target == null) {
            throw  new CustomException(CustomExceptionCode.USER_NOT_EXIST);
        }
        if (target.getId() == getUserId(request)){
            throw  new CustomException(CustomExceptionCode.SEND_REPEAT);
        }

        Message message = new Message();
        message.setFromId(getUserId(request));
        message.setToId(target.getId());
        //设置会话id,用户id小的在前面
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(letterDto.getContent());
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return new Result().success("发送成功");

    }

    //某主题的通知列表
    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public Result getNoticeDetail(@PathVariable("topic") String topic, @RequestParam(defaultValue = "1") int currentPage) {
        User user = userService.findUserById(getUserId(request));

        int total = messageService.findNoticeCount(user.getId(), topic);
        int offset = (currentPage-1)*5;

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, offset, 5);
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("notices", noticeVoList);
        data.put("total", total);
        data.put("currentPage", currentPage);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return new Result().success(data);
    }
*/

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (getUserId(request) == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (getUserId(request) == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }




}
