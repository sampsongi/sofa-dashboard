package com.chinaums.wh.job.manage.impl.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobRegistry;
import com.chinaums.wh.job.manage.impl.service.XxlJobInfoService;
import me.izhong.dashboard.manage.service.impl.CrudBaseServiceImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class XxlJobInfoServiceImpl extends CrudBaseServiceImpl<Long,XxlJobInfo> implements  XxlJobInfoService {
    @Override
    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("triggerNextTime").lte(maxNextTime));
        query.addCriteria(Criteria.where("triggerStatus").is(1));
        return super.selectList(query, null, null);
    }

    @Override
    public void scheduleUpdate(XxlJobInfo jobInfo) {
        Assert.notNull(jobInfo,"");
        Assert.notNull(jobInfo.getJobId(),"");
        Assert.notNull(jobInfo.getTriggerLastTime(),"");
        Assert.notNull(jobInfo.getTriggerNextTime(),"");
        Assert.notNull(jobInfo.getTriggerStatus(),"");

        Query query = new Query();
        query.addCriteria(Criteria.where("jobId").is(jobInfo.getJobId()));

        Update update = new Update();
        update.set("triggerLastTime",jobInfo.getTriggerLastTime());
        update.set("triggerNextTime",jobInfo.getTriggerNextTime());
        update.set("triggerStatus",jobInfo.getTriggerStatus());
        mongoTemplate.updateMulti(query, update, XxlJobRegistry.class);
    }
}
