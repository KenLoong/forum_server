package com.ken.forum_server.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;


@Data
@Document(indexName = "post", type = "_doc", shards = 6, replicas = 3) //建立索引
public class Post implements Serializable {
    @Id
    private Integer id;

    @Field(type = FieldType.Integer)
    private Integer userId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int tag;

    @Field(type = FieldType.Date)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    JsonFormat注解会使es的Field转换不了（它是需要整数的，format后会变成字符串？）
//    private LocalDateTime createTime;
    private Date createTime;

    private String createTimeStr;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Integer)
    private long likeCount;

    @Field(type = FieldType.Integer)
    private long collectCount;

    @Field(type = FieldType.Double)
    private double score;

    @Field(type = FieldType.Integer)
    private int status;
}
