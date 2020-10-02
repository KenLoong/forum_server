package com.ken.forum_server.dao;

import com.ken.forum_server.pojo.Post;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDao {

    /*private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private int type;
    private int tag;
    private Date createTime;
    private int commentCount;
    private double score;*/



    @Insert("insert into post (user_id,title,content,type,tag,create_time,comment_count,score)" +
            "values (#{userId},#{title},#{content},#{type},#{tag},#{createTime},#{commentCount},#{score})")
    @SelectKey(statement = "select last_insert_id()" ,keyProperty = "id",keyColumn = "id",resultType = int.class,before = false)
    public void addPost(Post post);

    @Select("select * from post where status != 2 order by type desc, create_time desc limit #{offset} , 5 ")
    @Results(id = "post",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "createTime",column = "create_time"),
            @Result(property = "commentCount",column = "comment_count")
    })
    List<Post> list(int offset);

    @Select("select * from post where status != 2 order by type desc , score desc , create_time desc limit #{offset} , 5 ")
    @ResultMap("post")
    List<Post> listByHot(int offset);

    @Select("select count(1) from post where status != 2")
    int count();

    @Select("select * from post where id = #{pid}")
    @ResultMap("post")
    Post findById(int pid);

    @Update("update post set comment_count = #{commentCount} where id = #{id}")
    void updateCommentCount(int id, int commentCount);

    @Select("select * from post where user_id = #{userId} order by id desc limit #{offset} , #{limit} ")
    List<Post> selectPosts(int userId, int offset, int limit);

    @Update("update post set type = #{type} where id = #{id}")
    int updateType(int id, int type);

    @Update("update post set status = #{status} where id = #{id}")
    int updateStatus(int id, int status);

    @Update("update post set score = #{score} where id = #{postId}")
    int updateScore(int postId, double score);

    @Select("select count(1) from post where user_id = #{userId} ")
    int selectUserPostsCount(int uid);

    @Delete("DELETE FROM post WHERE id = #{entityId}")
    void deteteById(int entityId);
}
