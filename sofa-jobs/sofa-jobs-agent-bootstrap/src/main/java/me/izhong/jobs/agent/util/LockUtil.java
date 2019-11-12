package me.izhong.jobs.agent.util;

import me.izhong.jobs.agent.service.JobServiceReference;
import me.izhong.jobs.manage.IJobMngFacade;

public class LockUtil {

    public static boolean lockKey(String key, long milliseconds){
        IJobMngFacade facade = ContextUtil.getBean(JobServiceReference.class).getJobMngFacade();
        return facade.lockKey(key,milliseconds);
    }

    public static void releaseKey(String key){
        IJobMngFacade facade = ContextUtil.getBean(JobServiceReference.class).getJobMngFacade();
        facade.releaseKey(key);
    }

}
