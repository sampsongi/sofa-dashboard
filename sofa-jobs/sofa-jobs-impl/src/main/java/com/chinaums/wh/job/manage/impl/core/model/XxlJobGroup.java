package com.chinaums.wh.job.manage.impl.core.model;

import com.chinaums.wh.db.common.domain.TimedBasedEntity;
import lombok.Data;
import com.chinaums.wh.db.common.annotation.AutoId;
import com.chinaums.wh.db.common.annotation.PrimaryId;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class XxlJobGroup  extends TimedBasedEntity implements Serializable {

    @AutoId
    @PrimaryId
    @Indexed(unique = true)
    private Long groupId;

    private String groupName;

    private int order;
    private List<String> addressList;   // 执行器地址列表，多地址逗号分隔(手动录入)
    private List<String> registryList;  // 执行器地址列表(系统注册)

}
