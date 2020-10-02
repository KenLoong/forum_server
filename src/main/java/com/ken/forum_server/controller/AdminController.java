package com.ken.forum_server.controller;

import com.ken.forum_server.common.Result;
import com.ken.forum_server.service.ElasticSearchService;
import com.ken.forum_server.service.UserService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RestController
public class AdminController extends BaseController{

    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private UserService userService;

    @RequiresRoles({"admin"})
    @PostMapping("/resetEs")
    public Result resetEs(){
        elasticSearchService.deleteAll();
        return new Result().success("es库重置成功");
    }

    @RequiresRoles({"admin"})
    @GetMapping("/deleteUser")
    public Result deleteUser(@RequestParam("uid") int uid ){
        userService.deleteUserById(uid);
        return new Result().success("删除用户成功");
    }
}
