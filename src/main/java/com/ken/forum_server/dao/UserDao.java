package com.ken.forum_server.dao;

import com.ken.forum_server.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

    @Select("select * from user where username = #{username} ")
    User findUserByUsername(String username);

    @Insert("insert into user (username,password,email,gender,create_time,state,code) values(#{username},#{password},#{email},#{gender},#{createTime},#{state},#{code})")
    void addUser(User user);

    @Select("select * from user where id = #{id} ")
    @Results(id = "user",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "createTime",column = "create_time")
    })
    User findUserById(Integer id);

    @Select("select id from user where email = #{email} ")
    @ResultMap("user")
    User findUserByEmail(String email);

    @Update("update user set state = 1 where id = #{id}")
    void activeUser(int id);

    @Update("update user set avatar = #{s} where id = #{userId}")
    void upadteAvatar(int userId, String s);

    @Select("select * from user where username = #{toName} ")
    User findUserByName(String toName);

    @Update("update user set state = 0 , code = #{code} where id = #{id}")
    void deleteUserById(int uid , String code);
}
