package com.chinaums.wh.jobs.agent.service;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.chinaums.wh.job.manage.IJobMngFacade;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class JobServiceReference {

    @SofaReference(interfaceType = IJobMngFacade.class, uniqueId = "${service.unique.id}", binding = @SofaReferenceBinding(bindingType = "bolt"))
    private IJobMngFacade jobMngFacade;

}
