package me.izhong.dashboard.manage.service;

import me.izhong.dashboard.manage.entity.SysConfig;
import me.izhong.dashboard.manage.domain.PageModel;
import me.izhong.dashboard.manage.domain.PageRequest;

public interface SysConfigService extends CrudBaseService<Long,SysConfig> {

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String selectConfigByKey(String configKey);

    /**
     * 校验参数键名是否唯一
     *
     * @param sysConfig 参数信息
     * @return 结果
     */
    public boolean checkConfigKeyUnique(SysConfig sysConfig);
}
