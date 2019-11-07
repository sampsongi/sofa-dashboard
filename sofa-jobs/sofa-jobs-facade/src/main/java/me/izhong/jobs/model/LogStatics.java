package me.izhong.jobs.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogStatics implements Serializable {
    private static final long serialVersionUID = 42L;

    private long triggerId;
    private long jobId;
    private String logData;
    private String jobResult;

}