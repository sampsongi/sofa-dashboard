package me.izhong.jobs.manage.impl.core.util;

import me.izhong.jobs.manage.impl.core.model.XxlJobInfo;
import me.izhong.jobs.model.Job;
import org.springframework.beans.BeanUtils;

public class JobInfoUtil {

    public static Job toRpcBean(XxlJobInfo db){
        if(db == null)
            return null;
        Job job = new Job();
        BeanUtils.copyProperties(db,job);
        return job;
    }

    public static XxlJobInfo toDbBean(Job job){
        if(job ==null)
            return null;
        XxlJobInfo xInfo = new XxlJobInfo();
        BeanUtils.copyProperties(job,xInfo);
        return xInfo;
    }


}
