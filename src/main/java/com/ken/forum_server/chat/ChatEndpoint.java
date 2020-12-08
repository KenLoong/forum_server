package com.ken.forum_server.chat;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ken.forum_server.dto.MessageDto;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.MessageService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//configurator = SpringConfigurator.class意思是让spring组件可以注入
@ServerEndpoint(value = "/chat/{token}")
@Component
public class ChatEndpoint {

    //用来存储每一个客户端对象对应的ChatEndpoint对象
    private static Map<String,ChatEndpoint> onlineUsers = new ConcurrentHashMap<>();
    @Autowired
    private static MessageService messageService;
    @Autowired
    private static UserService userService;

    //和某个客户端连接对象，需要通过他来给客户端发送数据
    private Session session;
    private String userId;

    @OnOpen
    //连接建立成功调用
    public void onOpen(@PathParam("token") String token, Session session, EndpointConfig config) throws IOException {

        verify(token);

        //需要通知其他的客户端，将所有的用户的用户名发送给客户端
        this.session = session;

        userId = JwtUtil.getToken(token).getClaim("id").asString();

        //获取用户名
        //存储该链接对象,如果之前该用户就已经有过连接了，就关闭该链接
        if (onlineUsers.get(userId) != null){
            onlineUsers.get(userId).session.close();
        }
        onlineUsers.put(userId,this);


        System.out.println(userId + " 连接服务器啦~~~ ");
        System.out.println("在线总人数： "+onlineUsers.size());

    }

    private void broadcastAllUsers(String message) {
        try {
            //遍历 onlineUsers 集合
            Set<String> names = onlineUsers.keySet();
            for (String name : names) {
                //获取该用户对应的ChatEndpoint对象
                ChatEndpoint chatEndpoint = onlineUsers.get(name);
                //发送消息
                chatEndpoint.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private Set<String> getNames() {
        return onlineUsers.keySet();
    }

    @OnMessage
    //接收到消息时调用
    public void onMessage(String message, Session session) {
        try {
//            //获取客户端发送来的数据  {"toName":"张三","message":"你好"}
            ObjectMapper mapper = new ObjectMapper();
            MessageDto messageDto = mapper.readValue(message, MessageDto.class);
            int from_id = Integer.parseInt(messageDto.getFrom_id());
            int to_id = Integer.parseInt(messageDto.getTo_id());
            String conversation_id;
            if (from_id < to_id){
                conversation_id = from_id+"_"+to_id;
            }else {
                conversation_id = to_id+"_"+from_id;
            }

            Message dbMessage = new Message();
            dbMessage.setConversationId(conversation_id);
            dbMessage.setFromId(from_id);
            dbMessage.setToId(to_id);
            dbMessage.setCreateTime(new Date());
            dbMessage.setContent(messageDto.getContent());
            //录入到数据库
            messageService.addMessage(dbMessage);

            ChatEndpoint chatEndpoint = onlineUsers.get(messageDto.getTo_id());
            //对方不在线
            if (chatEndpoint == null)return;
            //将数据推送给指定的客户端
            chatEndpoint.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    //连接关闭时调用
    public void onClose(Session session) {
        //移除连接对象
        onlineUsers.remove(userId);
        System.out.println(userId + " 退出连接啦~~~ ");
        System.out.println("在线总人数： "+onlineUsers.size());
    }

    private void verify(String token){

        if (StringUtils.isEmpty(token)){
            throw new CustomException("请先登录");
        }

        try {
            JwtUtil.verify(token);
        }catch (SignatureVerificationException e){
            //签名不一致异常
            throw new CustomException(CustomExceptionCode.TOKEN_ERROR);
        }catch (TokenExpiredException e){
            //令牌过期异常
            throw new CustomException(CustomExceptionCode.TOKNE_EXPIRED);
        }catch (AlgorithmMismatchException e){
            //算法不匹配异常
            throw new CustomException(CustomExceptionCode.TOKEN_ERROR);
        }catch (InvalidClaimException e){
            //失效的payload异常
            throw new CustomException(CustomExceptionCode.TOKEN_ERROR);
        }catch (Exception e){
            throw new CustomException(CustomExceptionCode.TOKEN_ERROR);
        }
    }
}
