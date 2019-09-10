package me.izhong.dashboard.manage.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Document(collection = "sequence")
public class SysSeqInfo {

    @Id
    private String id;

    @Field
    private String collectionName;

    @Field
    private Long seqId;

    @Field
    private Long maxId;

}