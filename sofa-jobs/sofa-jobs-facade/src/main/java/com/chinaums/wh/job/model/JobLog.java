package com.chinaums.wh.job.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class JobLog implements Serializable {

    private Long jobLogId;

    private String jobName;
    private String jobKey;
    private Long jobId;
    private String jobGroup;

    /** 日志信息 */
    private String jobMessage;

    private String status;

    /** 异常信息 */
    private String exceptionInfo;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    // execute info
    private String executorAddress;
    private String executorHandler;
    private String executorParam;
    private String executorShardingParam;
    private int executorFailRetryCount;

    // trigger info
    private Date triggerTime;
    private int triggerCode;
    private String triggerMsg;

    // handle info
    private Date handleTime;
    private int handleCode;
    private String handleMsg;

    // alarm info
    private int alarmStatus;
}
