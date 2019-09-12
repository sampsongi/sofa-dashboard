package com.chinaums.wh.job.manage.impl.core.conf;

import lombok.Getter;
import lombok.Setter;
import com.chinaums.wh.job.manage.impl.service.XxlJobGroupService;
import com.chinaums.wh.job.manage.impl.service.XxlJobInfoService;
import com.chinaums.wh.job.manage.impl.service.XxlJobLogService;
import com.chinaums.wh.job.manage.impl.service.XxlJobRegistryService;
import me.izhong.dashboard.job.core.biz.AdminBiz;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
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

    // conf
    @Value("${xxl.job.i18n}")
    private String i18n;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    //@Value("${spring.mail.username}")
    //private String emailUserName;

    // dao, service

    @Resource
    private XxlJobLogService xxlJobLogService;
    @Resource
    private XxlJobInfoService xxlJobInfoService;
    @Resource
    private XxlJobRegistryService xxlJobRegistryService;
    @Resource
    private XxlJobGroupService xxlJobGroupService;
    @Resource
    private AdminBiz adminBiz;
    //@Resource
    //private JavaMailSender mailSender;
    //@Resource
    //private DataSource dataSource;

}
