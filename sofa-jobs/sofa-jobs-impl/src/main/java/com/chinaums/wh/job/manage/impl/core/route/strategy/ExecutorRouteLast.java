package com.chinaums.wh.job.manage.impl.core.route.strategy;

import com.chinaums.wh.job.manage.impl.core.route.ExecutorRouter;
import me.izhong.dashboard.job.core.biz.model.ReturnT;
import me.izhong.dashboard.job.core.biz.model.TriggerParam;

import java.util.List;

public class ExecutorRouteLast extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return new ReturnT<String>(addressList.get(addressList.size()-1));
    }

}
