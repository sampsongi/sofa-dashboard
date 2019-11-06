package com.chinaums.wh.jobs.agent.log;

import com.chinaums.wh.job.manage.IJobMngFacade;
import com.chinaums.wh.job.model.LogStatics;
import com.chinaums.wh.jobs.agent.bean.JobContext;
import com.chinaums.wh.jobs.agent.service.JobServiceReference;
import com.chinaums.wh.jobs.agent.util.ContextUtil;

import java.text.MessageFormat;

public class RemoteLog implements AgentLog {

    private long jobId;
    private long triggerId;

    public RemoteLog(long jobId, long triggerId){
        this.jobId = jobId;
        this.triggerId = triggerId;
    }

    public RemoteLog(JobContext jobContext){
        this.jobId = jobContext.getJobId();
        this.triggerId = jobContext.getTriggerId();
    }

    private IJobMngFacade jobMngFacade;

    @Override
    public void info(String s) {
        doLog(s);
    }

    @Override
    public void info(String s, Object... args) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(char c : s.toCharArray()) {
            sb.append(c);
            if( c == '{'){
                sb.append(i++);
            }
        }
        doLog(MessageFormat.format(sb.toString(), args));
    }

    @Override
    public void debug(String s) {
        doLog(s);
    }

    private void doLog(String s) {
        if(jobMngFacade == null)
            jobMngFacade = ContextUtil.getBean(JobServiceReference.class).getJobMngFacade();
        LogStatics statics = new LogStatics();
        statics.setJobId(jobId);
        statics.setTriggerId(triggerId);
        statics.setLogData(s);
        jobMngFacade.uploadStatics(statics);
    }

    public static void main(String[] args) {
        String s = "测试{0}";
        Object o1 = "s1";
        Object o2 = "s2";
        System.out.println(MessageFormat.format(s, o1,o2));
    }

}
