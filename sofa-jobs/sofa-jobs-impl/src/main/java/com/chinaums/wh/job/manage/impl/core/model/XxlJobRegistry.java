package com.chinaums.wh.job.manage.impl.core.model;

import lombok.Data;
import me.izhong.dashboard.manage.annotation.AutoId;
import me.izhong.dashboard.manage.annotation.PrimaryId;
import me.izhong.dashboard.manage.domain.TimedBasedEntity;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

@Data
public class XxlJobRegistry  extends TimedBasedEntity implements Serializable {

    @AutoId
    @PrimaryId
    @Indexed(unique = true)
    private Long jobRegistryId;

    private String registryGroup;
    private String registryKey;
    private String registryValue;

}
