package me.izhong.jobs.manage.impl.service.impl;

import me.izhong.db.common.service.CrudBaseServiceImpl;
import me.izhong.domain.PageModel;
import me.izhong.domain.PageRequest;
import me.izhong.jobs.manage.impl.core.model.XxlJobGroup;
import me.izhong.jobs.manage.impl.service.XxlJobGroupService;
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
