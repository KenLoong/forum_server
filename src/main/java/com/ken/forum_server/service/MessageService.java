package com.ken.forum_server.service;

import com.ken.forum_server.pojo.Message;

import java.util.List;

public interface MessageService {

    public List<Message> findConversations(int userId, int offset, int limit);

    public int findConversationCount(int userId);

    public List<Message> findLetters(String conversationId, int offset, int limit);

    public int findLetterCount(String conversationId);

    public int findLetterUnreadCount(int userId, String conversationId);

    public int findLetterUnreadCount(int userId);

    //添加消息
    public int addMessage(Message message);

    //读消息
    public int readMessage(List<Integer> ids);

    public Message findLatestNotice(int userId, String topic);

    public int findNoticeCount(int userId, String topic);

    public int findNoticeUnreadCount(int userId, String topic);

    public List<Message> findNotices(int userId, String topic, int offset, int limit) ;

    List<Message> findAllMessage(Integer id, String topicComment, int offset, int limit);
}
