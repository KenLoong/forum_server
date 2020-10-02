package com.ken.forum_server.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleDao {

    @Select("select r.name from role r, user u where u.id = #{id} and r.id = u.rid ")
    Set<String> queryRoleNamesByUserId(int id);
}
