package com.chinaums.wh.job.manage.impl.service.impl;

import com.chinaums.wh.job.manage.impl.core.model.XxlJobLogGlue;
import com.chinaums.wh.job.manage.impl.service.XxlJobLogGlueService;
import me.izhong.dashboard.manage.service.impl.CrudBaseServiceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XxlJobLogGlueServiceImpl extends CrudBaseServiceImpl<Long,XxlJobLogGlue> implements XxlJobLogGlueService {

    @Override
    public List<XxlJobLogGlue> findByJobId(long jobId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jobId").is(jobId));
        return super.selectList(query, null, null);
    }

    @Override
    public void removeOld(Long jobId, int keepDays) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jobId").is(jobId));

        query.skip(keepDays);
        query.with(new Sort(Sort.Direction.DESC, "createTime"));
        mongoTemplate.remove(query, XxlJobLogGlue.class);
    }
}
