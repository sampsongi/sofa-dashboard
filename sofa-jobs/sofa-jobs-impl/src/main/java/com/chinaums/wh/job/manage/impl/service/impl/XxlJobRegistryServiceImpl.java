package com.chinaums.wh.job.manage.impl.service.impl;

import com.chinaums.wh.db.common.service.CrudBaseServiceImpl;
import com.mongodb.client.result.UpdateResult;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobRegistry;
import com.chinaums.wh.job.manage.impl.service.XxlJobRegistryService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XxlJobRegistryServiceImpl extends CrudBaseServiceImpl<Long,XxlJobRegistry> implements XxlJobRegistryService {
    @Override
    public void registryDelete(String registryGroup, String registryKey, String registryValue) {
        Query query = new Query();
        query.addCriteria(Criteria.where("registryGroup").is(registryGroup));
        query.addCriteria(Criteria.where("registryKey").is(registryKey));
        query.addCriteria(Criteria.where("registryValue").is(registryValue));
        mongoTemplate.remove(query, XxlJobRegistry.class);
    }

    @Override
    public void registrySave(String registGroup, String registryKey, String registryValue) {
        Assert.notNull(registGroup,"");
        Assert.notNull(registryKey,"");
        Assert.notNull(registryValue,"");
        XxlJobRegistry registry = new XxlJobRegistry();
        registry.setRegistryGroup(registGroup);
        registry.setRegistryKey(registryKey);
        registry.setRegistryValue(registryValue);
        mongoTemplate.save(registry);
    }

    @Override
    public long registryUpdate(String registryGroup, String registryKey, String registryValue) {
        Query query = new Query();
        query.addCriteria(Criteria.where("registryGroup").is(registryGroup));
        query.addCriteria(Criteria.where("registryKey").is(registryKey));
        query.addCriteria(Criteria.where("registryValue").is(registryValue));

        Update update = new Update();
        update.set("updateTime",new Date());
        UpdateResult ur = mongoTemplate.updateMulti(query, update, XxlJobRegistry.class);
        return ur.getModifiedCount();
    }

    @Override
    public List<Long> findDead(int deadTimeout) {
        Query query = new Query();
        query.addCriteria(Criteria.where("updateTime").lt(System.currentTimeMillis() - deadTimeout));
        List<XxlJobRegistry> ls = super.selectList(query, null, null);
        if(ls == null || ls.size() == 0)
            return null;
        return ls.stream().map(e -> e.getJobRegistryId()).collect(Collectors.toList());
    }

    @Override
    public List<XxlJobRegistry> findNormal(int deadTimeout) {
        Query query = new Query();
        query.addCriteria(Criteria.where("updateTime").gt(System.currentTimeMillis() - deadTimeout));
        return super.selectList(query, null, null);
    }
}
