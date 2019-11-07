package me.izhong.jobs.manage.impl.service;

import me.izhong.db.common.service.CrudBaseService;
import me.izhong.jobs.manage.impl.core.model.XxlJobGroup;

import java.util.List;

public interface XxlJobGroupService extends CrudBaseService<Long,XxlJobGroup> {
    List<XxlJobGroup> findByAddressType(int i);
}
