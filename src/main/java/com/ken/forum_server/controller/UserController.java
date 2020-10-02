package com.ken.forum_server.controller;

import com.ken.forum_server.annotation.TokenFree;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.FollowService;
import com.ken.forum_server.service.LikeService;
import com.ken.forum_server.service.PostService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.ConstantUtil;
import com.ken.forum_server.util.JwtUtil;
import com.ken.forum_server.vo.PaginationVo;
import com.ken.forum_server.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private PostService postService;


    /**
     *
     * @param user
     * @return 返回token信息，包含用户名及id
     */
    @TokenFree
    @RequestMapping("/login")
    public Result login(@RequestBody User user){
        return userService.login(user);
    }

    @TokenFree
    @RequestMapping("/register")
    public Result register(@RequestBody User user)
    {
        return userService.register(user);
    }


    @TokenFree
    @GetMapping("/active")
    public Result active(String username , String code)
    {
        return userService.active(username,code);
    }


    @RequestMapping("/test")
    public Result test(){
        return new Result();
    }

    @RequestMapping("/getInfo")
    public Result getInfo(HttpServletRequest request){
        String token = request.getHeader(JwtUtil.HEADER_TOKEN_KEY);
        System.out.println("token  + " + token);
        String id = JwtUtil.getToken(token).getClaim("id").asString();
        return userService.getInfo(Integer.parseInt(id));
    }


    /**
     * 上传头像
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/avatar")
    public Result updateAvatar(MultipartFile file) throws IOException {
//        String filePath = "static/img/avatar/";

//        String filePath = "src/main/resources/static/img/avatar/";
        String filePath = "/usr/share/nginx/html/dist/img/avatar/";
        //获取用户id
        int userId = getUserId(request);

        //判断文件夹目录是否存在
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isEmpty(suffix)) {
           return new Result().fail("上传照片格式不对！");
        }
        byte[] fileBytes = file.getBytes();
        FileOutputStream out = new FileOutputStream(filePath + userId+suffix);
        out.write(fileBytes);
        out.flush();
        out.close();
        //修改数据库数据
        userService.upadteAvatar(userId,userId+suffix);
        //返回用户头像名
        return new Result().success("/img/avatar/"+userId+suffix);

    }




    /**
     * 用户主页，无须登录也可访问，根据用户id来访问
     * @return
     */
    @TokenFree
    @RequestMapping("/profile/{uid}")
    public Result userPage(@PathVariable(name = "uid") int uid,
                           HttpServletRequest request){

        User user = userService.findUserById(uid);
        if (user == null) {
            throw new CustomException(CustomExceptionCode.USER_NOT_EXIST);
        }

        //用request的token信息来判断是否是访问别人的主页还是自己的主页
        Map<String,Object> map = new HashMap<>();
        boolean isLogin = isLogin(request);
        if (isLogin && getUserId(request) == uid){
            map.put("isMine",true);
        }else {
            map.put("isMine",false);
        }

        // 点赞数量
        int likeCount = likeService.findUserLikeCount(uid);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(uid, ConstantUtil.ENTITY_TYPE_USER);
        map.put("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ConstantUtil.ENTITY_TYPE_USER, uid);
        map.put("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (isLogin) {
            hasFollowed = followService.hasFollowed(getUserId(request), ConstantUtil.ENTITY_TYPE_USER, uid);
        }
        map.put("hasFollowed", hasFollowed);
        map.put("user",user);
        map.put("likeCount",likeCount);

        return new Result().success(map);
    }

    /**
     *查询用户文章
     * @return
     */
    @TokenFree
    @GetMapping("/userPost/{uid}")
    public Result posts(@PathVariable(name = "uid") int uid,@RequestParam(defaultValue = "1") int currentPage){
        PaginationVo<PostVo> pagination = postService.listByUserId(currentPage,uid);
        Map<String,Object> map = new HashMap<>();
        map.put("pagination",pagination);
        User user = userService.findUserById(uid);
        map.put("user",user);
        return new Result().success(map);
    }





}
