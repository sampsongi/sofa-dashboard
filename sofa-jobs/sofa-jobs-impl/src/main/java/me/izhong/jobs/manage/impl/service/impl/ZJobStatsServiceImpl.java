package me.izhong.jobs.manage.impl.service.impl;

import me.izhong.db.common.service.CrudBaseServiceImpl;
import me.izhong.jobs.manage.impl.core.model.XxlJobLogGlue;
import me.izhong.jobs.manage.impl.core.model.ZJobStats;
import me.izhong.jobs.manage.impl.service.XxlJobLogGlueService;
import me.izhong.jobs.manage.impl.service.ZJobStatsService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ZJobStatsServiceImpl extends CrudBaseServiceImpl<Long,ZJobStats>
        implements ZJobStatsService {

    @Override
    public List<ZJobStats> findByType(String type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type));
        return mongoTemplate.find(query, ZJobStats.class);
    }

    @Override
    public ZJobStats findByKey(String key) {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        return mongoTemplate.findOne(query, ZJobStats.class);
    }

    @Override
    public void removeStats(String key) {
        Assert.notNull(key,"");
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        mongoTemplate.remove(query, ZJobStats.class);
    }
}
