package com.chinaums.wh.jobs.agent.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class JobContext {

    private long jobId;
    private long triggerId;
    private long timeout;
    private Map<String,String> envs;
    private Map<String,String> params;

    public JobContext(){

    }

    public JobContext(long jobId, long triggerId, long timeout, Map<String,String> envs, Map<String,String> params){
        this.jobId = jobId;
        this.triggerId = triggerId;
        this.timeout = timeout;
        this.envs = envs;
        this.params = params;
    }
}
