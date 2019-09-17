package com.chinaums.wh.job.manage.impl.core.route.strategy;

import com.chinaums.wh.job.manage.impl.core.route.ExecutorRouter;
import com.chinaums.wh.job.model.TriggerParam;
import com.chinaums.wh.model.ReturnT;

import java.util.List;


public class ExecutorRouteFirst extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList){
        return new ReturnT<String>(addressList.get(0));
    }

}
