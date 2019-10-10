package com.chinaums.wh.job.manage.impl.service;

import com.chinaums.wh.db.common.service.CrudBaseService;
import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.manage.impl.core.model.XxlJobInfo;
import com.chinaums.wh.model.ReturnT;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface XxlJobInfoService extends CrudBaseService<Long,XxlJobInfo> {
    List<XxlJobInfo> scheduleJobQuery(long maxNextTime);

    void scheduleUpdate(XxlJobInfo jobInfo);

    public PageModel<XxlJobInfo> pageList(PageRequest request, XxlJobInfo jobInfo);

    /**
     * addJobScript job
     *
     * @param jobInfo
     * @return
     */
    public ReturnT<String> addJob(XxlJobInfo jobInfo);

    /**
     * updateJobGroup job
     *
     * @param jobInfo
     * @return
     */
    public ReturnT<String> updateJob(XxlJobInfo jobInfo);

    /**
     * removeJobGroup job
     * 	 *
     * @param id
     * @return
     */
    public ReturnT<String> removeJob(long id);

    /**
     * start job
     *
     * @param id
     * @return
     */
    public ReturnT<String> enableJob(long id);

    /**
     * stop job
     *
     * @param id
     * @return
     */
    public ReturnT<String> disableJob(long id);

    /**
     * dashboard info
     *
     * @return
     */
    public Map<String,Object> dashboardInfo();

    /**
     * chart info
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public ReturnT<Map<String,Object>> chartInfo(Date startDate, Date endDate);
}
