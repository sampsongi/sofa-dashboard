package com.chinaums.wh.job.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogStatics implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logId;
    private long jobId;
    private String logData;
    private String jobResult;

}