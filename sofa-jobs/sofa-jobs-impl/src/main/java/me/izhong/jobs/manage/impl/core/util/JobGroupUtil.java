package me.izhong.jobs.manage.impl.core.util;

import me.izhong.jobs.manage.impl.core.model.XxlJobGroup;
import me.izhong.jobs.model.JobGroup;
import org.springframework.beans.BeanUtils;

public class JobGroupUtil {

    public static JobGroup toRpcBean(XxlJobGroup db){
        if(db == null)
            return null;
        JobGroup jobGroup = new JobGroup();
        BeanUtils.copyProperties(db,jobGroup);
        return jobGroup;
    }

    public static XxlJobGroup toDbBean(JobGroup job){
        if(job ==null)
            return null;
        XxlJobGroup xxlJobGroup = new XxlJobGroup();
        BeanUtils.copyProperties(job,xxlJobGroup);
        return xxlJobGroup;
    }


}
