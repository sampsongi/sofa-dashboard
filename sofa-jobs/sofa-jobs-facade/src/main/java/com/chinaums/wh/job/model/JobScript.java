package com.chinaums.wh.job.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JobScript implements Serializable {

    private Long jobScriptId;// 任务主键ID
    private Long jobId;

    private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;
    private String glueRemark;
    private String addTime;
}
