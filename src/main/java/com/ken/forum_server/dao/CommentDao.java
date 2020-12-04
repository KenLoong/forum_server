package com.ken.forum_server.dao;

import com.ken.forum_server.pojo.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDao {

    @Select("select id, user_id, entity_type, entity_id, target_id, content, status, create_time from comment " +
            " where status = 0 " +
            " and entity_type = #{entityType}" +
            " and entity_id = #{entityId}" +
            " order by create_time asc" +
            " limit #{offset}, #{limit}")
    @Results(id = "comment",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "entityType",column = "entity_type"),
            @Result(property = "entityId",column = "entity_id"),
            @Result(property = "targetId",column = "target_id"),
            @Result(property = "createTime",column = "create_time")
    })
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    @Select("select count(id)" +
            " from comment" +
            " where status = 0" +
            " and entity_type = #{entityType}" +
            " and entity_id = #{entityId}")
    int selectCountByEntity(int entityType, int entityId);


    @Insert("insert into comment (user_id,entity_type,entity_id,target_id,content,status,create_time)"+
            " values(#{userId},#{entityType},#{entityId},#{targetId},#{content},#{status},#{createTime})")
    int insertComment(Comment comment);

    /**
     * 根据id查询评论
     * @param entityId
     * @return
     */
    @Select("select * from comment where id = #{entityId}")
    @ResultMap("comment")
    Comment findCommentById(int entityId);

    /**
     * 查找评论的内容
     * @param entityType
     * @param entityId
     * @return
     */
    @Select("select content from comment where  = #{entityId} and")
    @ResultMap("comment")
    String findCommentByIdAndType(int entityType, int entityId);
}
