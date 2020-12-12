# 论坛在线交流项目
这是一个论坛项目，我称之为ken社区。它是基于前后端分离的思想，采用springboot+vue开发的。用户可以在这个论坛里发表自己的文章，与其他人一起交流。
也可以关注别人，收藏别人的文章，点赞、回复评论别人的文章/评论，也可以与别人进行聊天（类似于网页版微信），是一个让大家可以一起交流、促进学习的地方。

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

## 后端技术栈
- Springboot
- Shiro
- Elasticsearch
- Mysql
- Redis
- Websocket
- jwt
- Mybatis

## 前端技术栈
- Vue
- ElementUI
- vue-router
- Websocket
- axios
- mavon-editor


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

## 项目预览图
![a](https://github.com/KenLoong/img_db/blob/main/20201210205131.png)
![a](https://github.com/KenLoong/img_db/blob/main/20201210205429.png)
![a](https://github.com/KenLoong/img_db/blob/main/20201210210216.png)
![a](https://github.com/KenLoong/img_db/blob/main/20201211104419.png)
![a](https://github.com/KenLoong/img_db/blob/main/20201211104653.png)
![a](https://github.com/KenLoong/img_db/blob/main/20201211110409.png)
![a](https://github.com/KenLoong/img_db/blob/main/4505c657d808ff74a65eeadc7443ce0.png)
![a](https://github.com/KenLoong/img_db/blob/main/80f2052c7750ab8b129ce8cbeea7f99.png)
![a](https://github.com/KenLoong/img_db/blob/main/8cdbf646c19a75f5c3db3c165c3706e.png)
![a](https://github.com/KenLoong/img_db/blob/main/db427cbf3ebf5098e84c269bae2233d.png)
![a](https://github.com/KenLoong/img_db/blob/main/f4772a46986e910e5a400d64126dc06.png)

