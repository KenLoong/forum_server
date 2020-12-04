package com.ken.forum_server.dao;

import com.ken.forum_server.pojo.Message;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDao {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
    //from_id == 1为系统通知
    @Select("select *" +
            " from message" +
            " where id in ( " +
            "            select max(id) from message" +
            "            where status != 2" +
            "            and from_id != 1" +
            "            and (from_id = #{userId} or to_id = #{userId})" +
            "            group by conversation_id" +
            "        )" +
            " order by id desc" +
            " limit #{offset}, #{limit}")
    @Results(id = "message",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "fromId",column = "from_id"),
            @Result(property = "toId",column = "to_id"),
            @Result(property = "conversationId",column = "conversation_id"),
            @Result(property = "createTime",column = "create_time")
    })
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量.
    @Select("select count(m.maxid) from (" +
            "            select max(id) as maxid from message" +
            "            where status != 2" +
            "            and from_id != 1" +
            "            and (from_id = #{userId} or to_id = #{userId})" +
            "            group by conversation_id" +
            "        ) as m")
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表.
    @Select("select * " +
            " from message" +
            " where status != 2" +
            " and from_id != 1" +
            " and conversation_id = #{conversationId}" +
            " order by id desc" +
            " limit #{offset}, #{limit}")
    @ResultMap("message")
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量.
    @Select("select count(id)" +
            "        from message" +
            "        where status != 2" +
            "        and from_id != 1" +
            "        and conversation_id = #{conversationId}")
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    @Select("select count(id)" +
            "        from message" +
            "        where status = 0" +
            "        and from_id != 1" +
            "        and to_id = #{userId}" +
            "        and conversation_id = #{conversationId}")
    int selectLetterUnreadCount(int userId, String conversationId);

    @Select("select count(id)" +
            "        from message" +
            "        where status = 0" +
            "        and from_id != 1" +
            "        and to_id = #{userId}")
    int selectLetterUnreadCountWithoutCid(int userId);


    // 新增消息
    int insertMessage(Message message);

    // 修改消息的状态
    int updateStatus(List<Integer> ids, int status);

    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题消息所包含的数量
    int selectNoticeCount(int userId, String topic);

    //查询某个主题消息所包含的数量
    int selectNoticeAllCount(int userId, String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(int userId, String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

    /**
     * 查询某个主题的所有消息
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findAllMessage(Integer userId, String topic, int offset, int limit);
}
