package com.ken.forum_server.service.impl;

import com.ken.forum_server.dao.MessageDao;
import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageDao.selectConversations(userId, offset, limit);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageDao.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageDao.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageDao.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId) {
        return messageDao.selectLetterUnreadCountWithoutCid(userId);
    }

    @Override
    public int addMessage(Message message) {
        System.out.println("执行了添加消息~~~！！！");
        return messageDao.insertMessage(message);
    }

    @Override
    public int  readMessage(List<Integer> ids) {
        return messageDao.updateStatus(ids, 1);
    }

    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageDao.selectLatestNotice(userId, topic);
    }

    //查询某个主题消息的数量（）
    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageDao.selectNoticeAllCount(userId, topic);
    }

    @Override
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageDao.selectNoticeUnreadCount(userId, topic);
    }

    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageDao.selectNotices(userId, topic, offset, limit);
    }

    @Override
    public List<Message> findAllMessage(Integer userId, String topic, int offset, int limit) {
        return messageDao.findAllMessage(userId,topic,offset,limit);
    }

    @Override
    public List<Integer> findChatedUsersIds(int userId) {
        return messageDao.selectChatedUsersIds(userId);
    }
}
