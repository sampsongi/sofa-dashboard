package me.izhong.dashboard.manage.service.impl;

import me.izhong.dashboard.manage.dao.NoticeDao;
import me.izhong.dashboard.manage.entity.SysNotice;
import me.izhong.dashboard.manage.expection.BusinessException;
import me.izhong.dashboard.manage.service.SysNoticeService;
import me.izhong.dashboard.manage.domain.PageModel;
import me.izhong.dashboard.manage.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysNoticeServiceImpl extends CrudBaseServiceImpl<Long,SysNotice> implements SysNoticeService {

}
