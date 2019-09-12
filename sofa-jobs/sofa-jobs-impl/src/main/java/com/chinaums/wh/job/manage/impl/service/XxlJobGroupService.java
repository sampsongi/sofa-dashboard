package com.chinaums.wh.job.manage.impl.service;

import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import me.izhong.dashboard.manage.service.CrudBaseService;

import java.util.List;

public interface XxlJobGroupService extends CrudBaseService<Long,XxlJobGroup> {
    List<XxlJobGroup> findByAddressType(int i);
}
