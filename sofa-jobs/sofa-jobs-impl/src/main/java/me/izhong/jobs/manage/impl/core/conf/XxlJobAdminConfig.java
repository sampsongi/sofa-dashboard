package me.izhong.jobs.manage.impl.core.conf;

import lombok.Getter;
import lombok.Setter;
import me.izhong.jobs.manage.impl.service.XxlJobGroupService;
import me.izhong.jobs.manage.impl.service.XxlJobInfoService;
import me.izhong.jobs.manage.impl.service.XxlJobLogService;
import me.izhong.jobs.manage.impl.service.XxlJobRegistryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Getter
@Setter
public class XxlJobAdminConfig implements InitializingBean {
    private static XxlJobAdminConfig adminConfig = null;
    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;
    }
    @Resource
    private XxlJobLogService xxlJobLogService;
    @Resource
    private XxlJobInfoService xxlJobInfoService;
    @Resource
    private XxlJobRegistryService xxlJobRegistryService;
    @Resource
    private XxlJobGroupService xxlJobGroupService;

}
