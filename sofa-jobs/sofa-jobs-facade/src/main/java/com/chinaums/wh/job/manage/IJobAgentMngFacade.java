package com.chinaums.wh.job.manage;

import com.chinaums.wh.domain.PageModel;
import com.chinaums.wh.domain.PageRequest;
import com.chinaums.wh.job.model.*;
import com.chinaums.wh.model.ReturnT;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IJobAgentMngFacade {

    ReturnT<String> kill(Long jobId, Long triggerId);

    ReturnT<String> trigger(Long jobId, Long triggerId, String script, Map<String,String> params);

}
