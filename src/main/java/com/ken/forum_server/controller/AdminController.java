package com.ken.forum_server.controller;

import com.ken.forum_server.common.Result;
import com.ken.forum_server.event.EventProducer;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.service.UserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ken.forum_server.util.ConstantUtil.TOPIC_RESET_ES;

@RequestMapping("/admin")
@RestController
public class AdminController extends BaseController{


    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;


//    @RequiresRoles({"admin"})
//    @PostMapping("/clearEs")
//    public Result clearEs(){
//        elasticSearchService.deleteAll();
//        return new Result().success("es库清空成功");
//    }

    @RequiresRoles({"admin"})
    @GetMapping("/deleteUser")
    public Result deleteUser(@RequestParam("uid") int uid ){
        userService.deleteUserById(uid);
        return new Result().success("删除用户成功");
    }

    @RequiresRoles({"admin"})
    @GetMapping("/resetEs")
    public Result resetEs(@RequestParam("uid") int uid){
        if (uid <= 0){
            return new Result().fail("uid需大于0");
        }

        //触发es重置事件
        Event event = new Event()
                .setTopic(TOPIC_RESET_ES)
                .setEntityId(uid);
        eventProducer.fireEvent(event);

        return new Result().success("重置成功");
    }
}
