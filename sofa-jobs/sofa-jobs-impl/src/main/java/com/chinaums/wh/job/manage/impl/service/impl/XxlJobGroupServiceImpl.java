package com.chinaums.wh.job.manage.impl.service.impl;

import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.service.XxlJobGroupService;
import me.izhong.dashboard.manage.service.impl.CrudBaseServiceImpl;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XxlJobGroupServiceImpl extends CrudBaseServiceImpl<Long,XxlJobGroup> implements XxlJobGroupService {
    @Override
    public List<XxlJobGroup> findByAddressType(int addressType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("addressType").is(addressType));
        return super.selectList(query, null, null);
    }
}
