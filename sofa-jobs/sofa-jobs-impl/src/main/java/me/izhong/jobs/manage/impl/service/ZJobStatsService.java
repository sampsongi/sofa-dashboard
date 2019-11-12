package me.izhong.jobs.manage.impl.service;

import me.izhong.db.common.service.CrudBaseService;
import me.izhong.jobs.manage.impl.core.model.ZJobStats;

import java.util.List;

public interface ZJobStatsService extends CrudBaseService<Long,ZJobStats> {

    ZJobStats insertOrUpdate(ZJobStats stats);

    List<ZJobStats> findByType(String type);

    ZJobStats findByKey(String key);

    boolean removeStats(String key);
}
