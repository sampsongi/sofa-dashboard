package com.chinaums.wh.db.common.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinaums.wh.db.common.annotation.CreateTimeAdvise;
import com.chinaums.wh.db.common.annotation.UpdateTimeAdvise;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class TimedBasedEntity implements Serializable {

    @Id
    @JSONField(serialize = false,deserialize = false)
    private ObjectId id;

    @Indexed(unique = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @CreateTimeAdvise
    private Date createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @UpdateTimeAdvise
    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Boolean isDelete;

    /**
     * 备注
     */
    private String remark;
}
