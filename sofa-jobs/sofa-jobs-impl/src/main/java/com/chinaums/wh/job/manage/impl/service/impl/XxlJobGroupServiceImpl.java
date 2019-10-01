package com.chinaums.wh.job.manage.impl.service.impl;

import com.chinaums.wh.db.common.service.CrudBaseServiceImpl;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobGroup;
import com.chinaums.wh.job.manage.impl.service.XxlJobGroupService;
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
