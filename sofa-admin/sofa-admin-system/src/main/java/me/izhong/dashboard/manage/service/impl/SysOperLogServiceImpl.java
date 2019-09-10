package me.izhong.dashboard.manage.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.izhong.dashboard.manage.dao.OperLogDao;
import me.izhong.dashboard.manage.entity.SysOperLog;
import me.izhong.dashboard.manage.service.CrudBaseService;
import me.izhong.dashboard.manage.service.SysOperLogService;
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
@Slf4j
public class SysOperLogServiceImpl extends CrudBaseServiceImpl<Long,SysOperLog> implements SysOperLogService {

}
