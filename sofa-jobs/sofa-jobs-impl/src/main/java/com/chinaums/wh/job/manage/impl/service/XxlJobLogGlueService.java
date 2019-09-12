package com.chinaums.wh.job.manage.impl.service;

import com.chinaums.wh.job.manage.impl.core.model.XxlJobLogGlue;
import me.izhong.dashboard.manage.service.CrudBaseService;
import org.bson.types.ObjectId;

import java.util.List;

public interface XxlJobLogGlueService extends CrudBaseService<Long,XxlJobLogGlue> {
    List<XxlJobLogGlue> findByJobId(long jobId);

    void removeOld(Long id, int i);
}
