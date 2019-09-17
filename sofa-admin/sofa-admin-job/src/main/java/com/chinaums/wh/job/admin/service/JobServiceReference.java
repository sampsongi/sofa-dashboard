package com.chinaums.wh.job.admin.service;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.chinaums.wh.job.manage.IJobGroupMngFacade;
import com.chinaums.wh.job.manage.IJobMngFacade;
import com.chinaums.wh.job.manage.IJobScriptMngFacade;
import org.springframework.stereotype.Service;

@Service
public class JobServiceReference {

    @SofaReference(interfaceType = IJobMngFacade.class, uniqueId = "${service.unique.id}", binding = @SofaReferenceBinding(bindingType = "bolt"))
    public IJobMngFacade jobService;

    @SofaReference(interfaceType = IJobScriptMngFacade.class, uniqueId = "${service.unique.id}", binding = @SofaReferenceBinding(bindingType = "bolt"))
    public IJobScriptMngFacade jobScriptService;

    @SofaReference(interfaceType = IJobGroupMngFacade.class, uniqueId = "${service.unique.id}", binding = @SofaReferenceBinding(bindingType = "bolt"))
    public IJobGroupMngFacade jobGroupService;
}
