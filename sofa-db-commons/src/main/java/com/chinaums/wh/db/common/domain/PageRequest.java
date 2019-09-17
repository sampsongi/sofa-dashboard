package com.chinaums.wh.db.common.domain;

import com.chinaums.wh.common.util.TimeUtil;
import com.chinaums.wh.model.UserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Data
@Slf4j
public class PageRequest {

    private long pageSize;
    private long pageNum;
    private String orderByColumn;
    private String isAsc;
    private String status;
    private Date beginDate;
    private Date endDate;

    private String perms;
    private Set<Long> depts;
    private UserInfo loginUser;

    private volatile AtomicBoolean alreadyInjectToQuery = new AtomicBoolean();

    public PageRequest() {

    }

    public PageRequest(long pageSize, long pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    public PageRequest(long pageSize, long pageNum, String orderByColumn, String isAsc) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.orderByColumn = orderByColumn;
        this.isAsc = isAsc;
    }

    public static PageRequest build() {
        return new PageRequest();
    }

    public PageRequest pageSize(long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageRequest pageNum(long pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public PageRequest orderBy(String column) {
        this.orderByColumn = column;
        return this;
    }

    public PageRequest isAsc(String isAsc) {
        this.isAsc = isAsc;
        return this;
    }

    public PageRequest status(String status) {
        this.status = status;
        return this;
    }

    public PageRequest beginDate(Date beginDate) {
        this.beginDate = beginDate;
        return this;
    }

    public PageRequest endDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
    public static PageRequest fromRequestIgnorePageSize(HttpServletRequest request) {
        return doFromRequest(request,true);
    }

    public static PageRequest fromRequest(HttpServletRequest request) {
        return doFromRequest(request,false);
    }


    private static PageRequest doFromRequest(HttpServletRequest request, boolean ignorePageSize) {
        PageRequest pageRequest = new PageRequest();
        if(ignorePageSize) {
            pageRequest.setPageSize(Integer.MAX_VALUE);
            pageRequest.setPageNum(1L);
        } else {
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageSize)) {
                try {
                    pageRequest.pageSize(Long.valueOf(pageSize));
                } catch (Exception e) {

                }
            }
            String pageNum = request.getParameter("pageNum");
            if (StringUtils.isNotBlank(pageNum)) {
                try {
                    pageRequest.pageNum(Long.valueOf(pageNum));
                } catch (Exception e) {

                }
            }
        }
        String orderByColumn = request.getParameter("orderByColumn");
        if (StringUtils.isNotBlank(orderByColumn)) {
            pageRequest.orderBy(orderByColumn);
        }
        String isAsc = request.getParameter("isAsc");
        if (StringUtils.isNotBlank(isAsc)) {
            pageRequest.isAsc(isAsc);
        }
        String status = request.getParameter("status");
        if (StringUtils.isNotBlank(status)) {
            pageRequest.status(status);
        }

        String beginTime = request.getParameter("beginTime");
        if (StringUtils.isNotBlank(beginTime)) {
            pageRequest.beginDate(TimeUtil.parseDate_yyyyMMdd_hl(beginTime));
        }

        String endTime = request.getParameter("endTime");
        if (StringUtils.isNotBlank(endTime)) {
            pageRequest.endDate(TimeUtil.parseDate_yyyyMMdd_hl(endTime));
        }

        return pageRequest;
    }

    public void injectQuery(Query query) {

        boolean succ  = alreadyInjectToQuery.compareAndSet(false,true);
        //if(!succ)
        //    return;
        //if (StringUtils.isNotBlank(status)) {
        //    query.addCriteria(Criteria.where("status").is(status));
        //}

        if (beginDate != null && endDate != null) {
            query.addCriteria(Criteria.where("createTime").gte(beginDate).lte(endDate));
        } else if (beginDate != null) {
            query.addCriteria(Criteria.where("createTime").gte(beginDate));
        } else if (endDate != null) {
            query.addCriteria(Criteria.where("createTime").lte(endDate));
        }

        if (StringUtils.isNotBlank(orderByColumn)) {
            query.with(new Sort(Sort.Direction.fromOptionalString(isAsc).orElse(Sort.Direction.ASC), orderByColumn));
        }
        if (pageNum < 1)
            pageNum = 1;
        if (pageSize <= 0)
            pageSize = 10;
        if (pageSize > Integer.MAX_VALUE)
            pageSize = Integer.MAX_VALUE;
        Pageable pageableRequest = org.springframework.data.domain.PageRequest.of((int) pageNum - 1, (int) pageSize);
        query.with(pageableRequest);

        if(false && depts != null && depts.size() > 0) {
            try {
                Field cField = FieldUtils.getField(query.getClass(), "criteria",true);
                cField.setAccessible(true);
                Object v = cField.get(query);
                Map<String, CriteriaDefinition> mq = (Map<String, CriteriaDefinition> )v;

                Criteria reqDeptCri = null;
                if (mq != null) {
                    Criteria[] criterias = mq.values().toArray(new Criteria[]{});
                    for(Criteria c : criterias){
                        if(StringUtils.equals(c.getKey(),"deptId")) {
                            reqDeptCri = c;
                            break;
                        }
                    }
                }

                if(reqDeptCri != null) {
                    //reqDeptCri.andOperator()
                    //Long[] xx = new Long[]{34L,35L};
                    //reqDeptCri.getCriteriaObject();
                    //reqDeptCri.in(xx);
                    reqDeptCri.andOperator(Criteria.where("deptId").in(depts));
                } else {
                    Criteria addCri = Criteria.where("deptId").in(depts);
                    query.addCriteria(addCri);
                }


                //log.info("{}",mq);

            } catch (Exception e) {
                log.error("",e);
            }
        }

    }

    public void injectAggregation(List<AggregationOperation> aggregationOperations) {
        boolean succ  = alreadyInjectToQuery.compareAndSet(false,true);
        //if(!succ)
        //    return;

        if (StringUtils.isNotBlank(status)) {
            aggregationOperations.add(match(Criteria.where("status").is(status)));
        }

        if (beginDate != null && endDate != null) {
            aggregationOperations.add(match(Criteria.where("createTime").gte(beginDate).lte(endDate)));
        } else if (beginDate != null) {
            aggregationOperations.add(match(Criteria.where("createTime").gte(beginDate)));
        } else if (endDate != null) {
            aggregationOperations.add(match(Criteria.where("createTime").lte(endDate)));
        }
        injectAggregationOnlyPage(aggregationOperations);
    }

    public void injectAggregationOnlyPage(List<AggregationOperation> aggregationOperations) {
        boolean succ  = alreadyInjectToQuery.compareAndSet(false,true);
        //if(!succ)
        //    return;

        if (StringUtils.isNotBlank(orderByColumn)) {
            aggregationOperations.add(sort(new Sort(Sort.Direction.fromOptionalString(isAsc).orElse(Sort.Direction.ASC), orderByColumn)));
        }
        if (pageNum < 1)
            pageNum = 1;
        if (pageSize > Integer.MAX_VALUE)
            pageSize = Integer.MAX_VALUE;
        Pageable pageableRequest = org.springframework.data.domain.PageRequest.of((int) pageNum - 1, (int) pageSize);
        aggregationOperations.add(skip((pageNum - 1) * pageSize));
        aggregationOperations.add(limit(pageSize));

        if(depts != null && depts.size() > 0) {
            aggregationOperations.add(match(Criteria.where("deptId").in(depts)));
        }
    }
}
