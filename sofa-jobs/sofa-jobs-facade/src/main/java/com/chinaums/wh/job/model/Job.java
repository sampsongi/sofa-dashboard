package com.chinaums.wh.job.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class Job implements Serializable {
    private String jobName;
    private String jobKey;
    private Long jobId;
    private String jobGroup;

    private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;		// GLUE源代码
    private String glueRemark;		// GLUE备注
    private Date glueUpdateTime;	// GLUE更新时间
}
