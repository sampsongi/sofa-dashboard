package me.izhong.jobs.manage.impl.service;

import me.izhong.db.common.service.CrudBaseService;
import me.izhong.jobs.manage.impl.core.model.XxlJobLog;

import java.util.Date;
import java.util.List;

public interface XxlJobLogService extends CrudBaseService<Long,XxlJobLog> {
    long triggerCountByHandleCode(int successCode);

    List<Long> findFailJobLogIds();

    long updateAlarmStatus(long failLogId, int oldStatus, int newStatus);

    void clearLog(Long jobId, Date clearBeforeTime, Integer clearBeforeNum);

    void clearLog(Long[] jobLogIds);
}
