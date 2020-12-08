package com.ken.forum_server.service;

import com.ken.forum_server.pojo.Message;

import java.util.List;
import java.util.Set;

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

    Set<Integer> findChatToMeIds(int userId);

    /**
     * 查找聊天记录
     * @param userId
     * @param currentId
     * @return
     */
    List<Message> findChatList(int userId, int currentId);

    /**
     * 查询我向对方发过消息的用户ids
     * @param userId
     * @return
     */
    Set<Integer> findIChatToids(int userId);
}
