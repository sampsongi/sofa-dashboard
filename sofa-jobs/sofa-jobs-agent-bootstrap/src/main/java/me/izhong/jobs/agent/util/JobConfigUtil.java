package me.izhong.jobs.agent.util;

import com.alipay.lookout.common.Assert;
import me.izhong.jobs.agent.service.JobServiceReference;
import me.izhong.jobs.manage.IJobMngFacade;

public class JobConfigUtil {

    public static String getConfigValue(String configKey){
        Assert.notNull(configKey,"configKey不能为空");
        IJobMngFacade facade = ContextUtil.getBean(JobServiceReference.class).getJobMngFacade();
        String st = facade.findConfigByKey(configKey);
        return st;
    }

}
