package com.ken.forum_server.es;

import com.ken.forum_server.pojo.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ElasticsearchRepository<Post, Integer> {

}
