package com.chinaums.wh.jobs.agent.job.context;

import com.chinaums.wh.jobs.agent.log.AgentLog;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Map;

@Setter
@Getter
public class ScriptRunContext {

    private long jobId;
    private long triggerId;
    private long timeout;
    private String script;
    private File scriptFile;
    private AgentLog log;
    private Map<String,String> envs;
    private Map<String,String> params;

    public ScriptRunContext(){

    }

    public ScriptRunContext(long jobId, long triggerId, long timeout, Map<String,String> envs, Map<String,String> params){
        this.jobId = jobId;
        this.triggerId = triggerId;
        this.timeout = timeout;
        this.envs = envs;
        this.params = params;
    }
}
