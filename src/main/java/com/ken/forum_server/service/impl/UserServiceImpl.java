package com.ken.forum_server.service.impl;

import com.ken.forum_server.async.EventHandler;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.dao.UserDao;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.shiro.JWToken;
import com.ken.forum_server.util.JwtUtil;
import com.ken.forum_server.util.MD5Util;
import com.ken.forum_server.util.MailUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ken.forum_server.util.ConstantUtil.TOPIC_REGISTER;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    MailUtil mailUtil;

    @Value("${qiniu.bucket.avatar.url}")
    private String avatarBucketUrl;

//    @Autowired
//    EventProducer eventProducer;


//    private String uploadPath = "/static/img/avatar/";


    @Override
    public Result login(User user) {
        if (user.getUsername() == null || user.getUsername().length() > 20){
            return new Result(101,"用户名不可为空且长度不可超过20");
        }

        if (user.getPassword() == null || user.getPassword().length() == 0 ){
            return new Result(102,"密码不可为空");
        }

        User dbUser = userDao.findUserByUsername(user.getUsername());
        if( dbUser  == null){
            return new Result(102,"用户不存在！");
        }

        String encryption = MD5Util.md5Encryption(user.getPassword());
        if (!encryption.equals(dbUser.getPassword())){
            return new Result(103,"用户名或密碼错误！");
        }

        if (dbUser.getState() == 0){
            return new Result(104,"您尚未激活账号，请往注册邮箱处点击链接激活");
        }

        //生成token
        Map<String,String> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("id",dbUser.getId()+"");

        JWToken jwToken = new JWToken(JwtUtil.getToken(map,user.getUsername()));
        try {
            //让shiro缓存用户信息
            SecurityUtils.getSubject().login(jwToken);
        } catch (UnknownAccountException e) {
            return new Result(101,"用户不存在");
        } catch (IncorrectCredentialsException e) {
            return new Result(102,"用户名或密码错误");
        }

        return new Result().success("登陆成功",jwToken.getCredentials());
    }

    @Override
    public Result register(User user) {

        if (user.getUsername() == null || user.getUsername().length() > 20){
            return new Result(101,"用户名不可为空且长度不可超过20");
        }

        if (user.getPassword() == null || user.getPassword().length() > 20){
            return new Result(102,"密码不可为空且长度不可超过20");
        }

        if( userDao.findUserByUsername(user.getUsername()) != null){
            return new Result(103,"用户名已被注册，请换一个");
        }

        if (userDao.findUserByEmail(user.getEmail()) != null ){
            return new Result(104,"邮箱已被注册，请换一个");
        }


        //对密码进行加密
        String newPassword = MD5Util.md5Encryption(user.getPassword());
        user.setPassword(newPassword);
        user.setCreateTime(new Date());
        user.setCode(UUID.randomUUID().toString());
        user.setState(0);
        user.setAvatar("http://"+avatarBucketUrl+"/default.jpg");

//        //触发注册事件
//        Event event = new Event()
//                .setTopic(TOPIC_REGISTER)
//                .setData("user",user);
//        //用kafka
////        eventProducer.fireEvent(event);
//        EventHandler.handleTask(event);
        try {
            mailUtil.registerMail(user.getEmail(),"欢迎来到ken社区",user.getCode(),user.getUsername());
        } catch (MessagingException e) {
            e.printStackTrace();
            return new Result().fail(e.getMessage());
        }


        //发送邮件
        //mailUtil.sendMail(user.getEmail(),"欢迎来到ken社区",user.getCode(),user.getUsername());
        userDao.addUser(user);
        return new Result(200,"注册成功");
    }

    //获取用户信息
    @Override
    public Result getInfo(int id) {
        User user = userDao.findUserById(id);
        return new Result().success(user);
    }

    @Override
    public Result active(String username, String code) {
        User user = userDao.findUserByUsername(username);
        if (user.getCode().equals(code)){
            userDao.activeUser(user.getId());
            return new Result().success("激活成功");
        }else {
            return new Result(501,"激活失败");
        }
    }

    @Override
    public void upadteAvatar(int userId, String s) {
        userDao.upadteAvatar(userId,s);
    }

    @Override
    public User findUserById(Integer userId) {
        User user = userDao.findUserById(userId);
        return user;

    }

    @Override
    public User findUserByName(String toName) {
        return userDao.findUserByName(toName);
    }

    @Override
    public void deleteUserById(int uid) {
        userDao.deleteUserById(uid , "ssssdddd");
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public void updatePassword(User user) {
        userDao.updatePassword(user);
    }

    @Override
    public String getUserPasswordById(int userId) {
        return userDao.getPasswordById(userId);
    }

}
