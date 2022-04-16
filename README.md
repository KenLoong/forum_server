# 论坛在线交流项目

这是一个论坛项目，我称之为ken社区。它是基于前后端分离的思想，采用springboot+vue开发的。用户可以在这个论坛里发表自己的文章，与其他人一起交流。 也可以关注别人，收藏别人的文章，点赞、回复评论别人的文章/评论，也可以与别人进行聊天（类似于网页版微信），是一个让大家可以一起交流、促进学习的地方。

## 

## 功能实现：

- 发表文章（markdown格式，富文本编辑，可上传图片）
- 评论文章
- 回复评论
- 邮件发送（注册时发送确认邮件）
- 关注
- 收藏
- 点赞
- 搜索
- 聊天（类似网页版微信）

## 

## 前端工程地址：https://github.com/KenLoong/forum_front

## 

## 后端工程地址：https://github.com/KenLoong/forum_server

## 

## 后端技术栈

- Springboot
- Shiro
- Elasticsearch
- Mysql
- Redis
- Websocket
- jwt
- Mybatis

## 

## 前端技术栈

- Vue
- ElementUI
- vue-router
- Websocket
- axios
- mavon-editor

## 

## 技术实现

- 采用jwt、自定义注解、拦截器组合的方式来进行登录校验
- 采用shiro框架来进行权限控制（对文章进行置顶、加精）
- 采用websocket技术实现即时聊天
- 采用redis数据库来存储点赞、关注、收藏等信息，提高系统运行速度
- 采用elasticsearch对文章进行索引，实现搜索功能
- 采用线程池来实现通知消息的异步写入
- 采用vue-cli来搭建前端项目，利用axios来做异步请求，vuex存储用户信息
- 采用七牛云服务器来实现图片的存储
- 采用element-ui,mavon-editor(富文本编辑)，bootstrap来美化用户视觉

## 

## 项目预览图

发表文章 [![发表文章](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E5%86%99%E6%96%87%E7%AB%A0.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/写文章.png)

搜索文章 [![搜索文章](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E6%90%9C%E7%B4%A2.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/搜索.png)

个人主页 [![个人主页](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E4%B8%AA%E4%BA%BA%E4%B8%BB%E9%A1%B5.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/个人主页.png)

粉丝页面 [![粉丝页面](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E5%85%B3%E6%B3%A8%E4%B8%8E%E7%B2%89%E4%B8%9D.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/关注与粉丝.png)

最热文章 [![最热文章](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E6%9C%80%E7%83%AD%E6%96%87%E7%AB%A0.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/最热文章.png)

最新文章 [![最新文章](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E9%A6%96%E9%A1%B5.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/首页.png)

聊天 [![聊天](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E8%81%8A%E5%A4%A9.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/聊天.png)

评论、回复 [![评论](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E8%AF%84%E8%AE%BA.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/评论.png)

设置页面 [![设置页面](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E4%B8%AA%E4%BA%BA%E8%B5%84%E6%96%99.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/个人资料.png)

别人的主页 [![别人的主页](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E4%B8%BB%E9%A1%B5.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/主页.png)

消息中心 [![消息中心](https://github.com/hycldew/forum_server/raw/master/readme-picture/%E9%80%9A%E7%9F%A5.png)](https://github.com/hycldew/forum_server/blob/master/readme-picture/通知.png)

