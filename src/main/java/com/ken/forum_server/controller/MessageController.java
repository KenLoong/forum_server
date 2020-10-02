package com.ken.forum_server.controller;

import com.alibaba.fastjson.JSONObject;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dto.LetterDto;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.MessageService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.vo.LetterVo;
import com.ken.forum_server.vo.MessageVo;
import com.ken.forum_server.vo.PaginationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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

    // 会话列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public Result getLetterList(@RequestParam(defaultValue = "1") int currentPage) {
        int userId = getUserId(request);
        User user = userService.findUserById(userId);

        //查询用户有几个会话
        int conversationCount = messageService.findConversationCount(userId);

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


    //通知列表
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public Result getNoticeList() {
        User user = userService.findUserById(getUserId(request));

        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String,Object> map = new HashMap<>();
        Map<String, Object> commentMap = new HashMap<>();
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
        map.put("noticeUnreadCount", noticeUnreadCount);

        return new Result().success(map);
    }


    //会话详情列表
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