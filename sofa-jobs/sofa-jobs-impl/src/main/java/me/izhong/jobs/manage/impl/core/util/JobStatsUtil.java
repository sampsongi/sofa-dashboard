package me.izhong.jobs.manage.impl.core.util;

import me.izhong.jobs.manage.impl.core.model.ZJobStats;
import me.izhong.jobs.model.JobStats;
import org.springframework.beans.BeanUtils;

public class JobStatsUtil {

    public static JobStats toRpcBean(ZJobStats db){
        if(db == null)
            return null;
        JobStats jobGroup = new JobStats();
        BeanUtils.copyProperties(db,jobGroup);
        return jobGroup;
    }

    public static ZJobStats toDbBean(JobStats job){
        if(job ==null)
            return null;
        ZJobStats xxlJobGroup = new ZJobStats();
        BeanUtils.copyProperties(job,xxlJobGroup);
        return xxlJobGroup;
    }


}
