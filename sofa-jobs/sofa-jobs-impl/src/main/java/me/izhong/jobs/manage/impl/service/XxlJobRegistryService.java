package me.izhong.jobs.manage.impl.service;

import me.izhong.db.common.service.CrudBaseService;
import me.izhong.jobs.manage.impl.core.model.XxlJobRegistry;

import java.util.List;

public interface XxlJobRegistryService extends CrudBaseService<Long,XxlJobRegistry> {
    void registryDelete(String registGroup, String registryKey, String registryValue);

    void registrySave(String registGroup, String registryKey, String registryValue);

    long registryUpdate(String registGroup, String registryKey, String registryValue);

    List<Long> findDead(int deadTimeout);

    List<XxlJobRegistry> findNormal(int deadTimeout);
}
