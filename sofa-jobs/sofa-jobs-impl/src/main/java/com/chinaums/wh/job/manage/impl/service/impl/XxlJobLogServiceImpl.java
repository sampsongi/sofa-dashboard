package com.chinaums.wh.job.manage.impl.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobLog;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobRegistry;
import com.chinaums.wh.job.manage.impl.service.XxlJobLogService;
import me.izhong.dashboard.manage.service.impl.CrudBaseServiceImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XxlJobLogServiceImpl extends CrudBaseServiceImpl<Long,XxlJobLog> implements XxlJobLogService {
    @Override
    public long triggerCountByHandleCode(int successCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("triggerCode").is(successCode));
        return super.count(query,null,null);
    }

    @Override
    public List<Long> findFailJobLogIds() {
        Query query = new Query();
        Criteria c1 = Criteria.where("triggerCode").in(0,200).andOperator( Criteria.where("handleCode").is(0)  );
        Criteria c2 = Criteria.where("handleCode").is(200);
        Criteria dest = c1.orOperator(c2).not();
        query.addCriteria(dest);
        List<XxlJobLog> ls = super.selectList(query,null,null);
        if(ls ==null || ls.size() == 0)
            return new ArrayList<>();
        return ls.stream().map(e->e.getJobId()).collect(Collectors.toList());

    }

    @Override
    public long updateAlarmStatus(long failLogId, int oldStatus, int newStatus) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jobLogId").is(failLogId));
        query.addCriteria(Criteria.where("alarmStatus").is(oldStatus));

        Update update = new Update();
        update.set("alarmStatus",newStatus);
        UpdateResult ur = mongoTemplate.updateMulti(query, update, XxlJobRegistry.class);
        return ur.getModifiedCount();
    }

    @Override
    public void clearLog(Long jobGroup, Long jobId, Date clearBeforeTime, int clearBeforeNum) {
        Query query = new Query();
        if(jobGroup != null)
            query.addCriteria(Criteria.where("jobGroup").is(jobGroup));
        if(jobId != null)
            query.addCriteria(Criteria.where("jobId").is(jobId));
        if(clearBeforeTime !=null)
            query.addCriteria(Criteria.where("createTime").lte(clearBeforeTime));
        if(clearBeforeNum > 0)
            query.addCriteria(Criteria.where("jobId").lte(clearBeforeNum));
        mongoTemplate.remove(query, XxlJobRegistry.class);
    }
}
