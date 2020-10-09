package com.ken.forum_server.service;

import com.ken.forum_server.pojo.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ElasticSearchService {

    public void savePost(Post post);

    public void deletePost(int id);

    public Page<Post> searchPost(String keyword, int current, int limit) ;

    void deleteAll();

    void saveAllPost(List<Post> posts);
}
