package me.izhong.jobs.manage.impl.service;

import me.izhong.db.common.service.CrudBaseService;
import me.izhong.jobs.manage.impl.core.model.XxlJobLogGlue;

import java.util.List;

public interface XxlJobLogGlueService extends CrudBaseService<Long,XxlJobLogGlue> {
    List<XxlJobLogGlue> findByJobId(long jobId);

    void removeOld(Long id, int i);
}
