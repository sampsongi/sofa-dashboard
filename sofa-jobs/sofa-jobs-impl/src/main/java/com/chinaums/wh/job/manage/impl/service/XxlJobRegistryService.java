package com.chinaums.wh.job.manage.impl.service;

import com.chinaums.wh.db.common.service.CrudBaseService;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobRegistry;

import java.util.List;

public interface XxlJobRegistryService extends CrudBaseService<Long,XxlJobRegistry> {
    void registryDelete(String registGroup, String registryKey, String registryValue);

    void registrySave(String registGroup, String registryKey, String registryValue);

    long registryUpdate(String registGroup, String registryKey, String registryValue);

    List<Long> findDead(int deadTimeout);

    List<XxlJobRegistry> findNormal(int deadTimeout);
}
