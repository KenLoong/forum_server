package com.ken.forum_server.controller;

import com.ken.forum_server.annotation.TokenFree;
import com.ken.forum_server.common.Result;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.service.ElasticSearchService;
import com.ken.forum_server.service.LikeService;
import com.ken.forum_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ken.forum_server.util.ConstantUtil.ENTITY_TYPE_POST;

@RestController
public class SearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @TokenFree
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public Result search(String keyword, @RequestParam(defaultValue = "1") int currentPage) {

        // 搜索帖子
        Page<Post> searchResult =
                elasticSearchService.searchPost(keyword, currentPage - 1, 5);
        // 聚合数据
        List<Map<String, Object>> Posts = new ArrayList<>();
        if (searchResult != null) {
            for (Post post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);


                //转化日期格式
                String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                post.setCreateTimeStr(sdf.format(post.getCreateTime()));

                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                Posts.add(map);
            }
        }

        Map<String,Object> data = new HashMap<>();
        data.put("posts",Posts);
        data.put("keyword",keyword);
        data.put("currentPage",currentPage);
        data.put("total",searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return new Result().success(data);
    }
}
