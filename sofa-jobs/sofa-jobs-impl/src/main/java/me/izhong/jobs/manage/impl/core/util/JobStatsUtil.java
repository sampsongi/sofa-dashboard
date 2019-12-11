package me.izhong.jobs.manage.impl.core.util;

import me.izhong.jobs.manage.impl.core.model.ZJobStats;
import me.izhong.jobs.model.JobStats;
import org.springframework.beans.BeanUtils;

public class JobStatsUtil {

    public static JobStats toRpcBean(ZJobStats db){
        if(db == null)
            return null;
        JobStats jobStats = new JobStats();
        BeanUtils.copyProperties(db,jobStats);
        return jobStats;
    }

    public static ZJobStats toDbBean(JobStats job){
        if(job == null)
            return null;
        ZJobStats zJobStats = new ZJobStats();
        BeanUtils.copyProperties(job,zJobStats);
        return zJobStats;
    }


}
