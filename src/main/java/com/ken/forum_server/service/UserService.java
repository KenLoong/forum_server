package com.ken.forum_server.service;

import com.ken.forum_server.common.Result;
import com.ken.forum_server.pojo.User;

public interface UserService {

    Result login(User user);

    Result register(User user);

    Result getInfo(int id);

    Result active(String username , String code);

    void upadteAvatar(int userId, String s);

    User findUserById(Integer userId);

    User findUserByName(String toName);

    void deleteUserById(int uid);

    User findUserByEmail(String eamil);

    void updatePassword(User user);

    String getUserPasswordById(int userId);

}
