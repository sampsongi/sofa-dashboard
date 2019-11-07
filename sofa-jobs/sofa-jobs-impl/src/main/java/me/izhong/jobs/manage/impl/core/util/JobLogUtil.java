package me.izhong.jobs.manage.impl.core.util;

import me.izhong.jobs.manage.impl.core.model.XxlJobLog;
import me.izhong.jobs.model.JobLog;
import me.izhong.jobs.manage.impl.core.model.XxlJobLog;
import org.springframework.beans.BeanUtils;

public class JobLogUtil {

    public static JobLog toRpcBean(XxlJobLog db){
        if(db == null)
            return null;
        JobLog job = new JobLog();
        BeanUtils.copyProperties(db,job);
        return job;
    }

    public static XxlJobLog toDbBean(JobLog job){
        if(job ==null)
            return null;
        XxlJobLog xInfo = new XxlJobLog();
        BeanUtils.copyProperties(job,xInfo);
        return xInfo;
    }


}
