package me.izhong.jobs.manage.impl;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import me.izhong.jobs.manage.IJobAgentMngFacade;
import me.izhong.jobs.manage.IJobMngFacade;
import org.springframework.stereotype.Service;

@Service
public class JobAgentServiceReference {

    @SofaReference(interfaceType = IJobAgentMngFacade.class,
            uniqueId = "${service.unique.id}",
            jvmFirst = false,
            binding = @SofaReferenceBinding(bindingType = "bolt",timeout = 20000))
    public IJobAgentMngFacade jobAgentService;

}
