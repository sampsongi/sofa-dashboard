package com.chinaums.wh.job.manage.impl.service;

import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import me.izhong.dashboard.manage.service.CrudBaseService;

import java.util.List;

public interface XxlJobInfoService extends CrudBaseService<Long,XxlJobInfo> {
    List<XxlJobInfo> scheduleJobQuery(long maxNextTime);

    void scheduleUpdate(XxlJobInfo jobInfo);
}
