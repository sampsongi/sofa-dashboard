package me.izhong.dashboard.manage.service.impl;

import com.chinaums.wh.db.common.service.CrudBaseServiceImpl;
import me.izhong.dashboard.manage.dao.ConfigDao;
import me.izhong.dashboard.manage.entity.SysConfig;
import me.izhong.dashboard.manage.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImpl extends CrudBaseServiceImpl<Long,SysConfig> implements SysConfigService {

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数名称
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        SysConfig sysConfig = configDao.findByConfigKey(configKey);
        if (sysConfig == null) {
            return null;
        }
        return sysConfig.getConfigValue();

    }

    /**
     * 校验参数键名是否唯一
     *
     * @param sysConfig 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig sysConfig) {
        Long configId = sysConfig.getConfigId() == null ? -1L : sysConfig.getConfigId();
        SysConfig info = configDao.findByConfigKey(sysConfig.getConfigKey());
        if (info != null && info.getConfigId().longValue() != configId.longValue()) {
            return false;
        }
        return true;
    }
}
