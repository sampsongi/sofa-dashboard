package com.chinaums.wh.job.admin.service;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.chinaums.wh.job.manage.IJobMngFacade;
import org.springframework.stereotype.Service;

@Service
public class JobServiceReference {

    @SofaReference(interfaceType = IJobMngFacade.class,
            uniqueId = "${service.unique.id}",
            jvmFirst = false,
            binding = @SofaReferenceBinding(bindingType = "bolt",timeout = 20000))
    public IJobMngFacade jobService;

}
