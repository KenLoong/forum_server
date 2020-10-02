package com.ken.forum_server.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureDao {

    @Insert("insert into picture (pid , path) values(#{pid},#{fileName})")
    void addPicture(int pid, String fileName);

    @Select("select path from picture where pid = #{id}")
    List<String> findByPid(Integer id);
}
