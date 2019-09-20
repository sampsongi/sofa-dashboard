package com.chinaums.wh.job.manage.impl.service;

import com.chinaums.wh.db.common.service.CrudBaseService;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;

import java.util.List;

public interface XxlJobGroupService extends CrudBaseService<Long,XxlJobGroup> {
    List<XxlJobGroup> findByAddressType(int i);
}
