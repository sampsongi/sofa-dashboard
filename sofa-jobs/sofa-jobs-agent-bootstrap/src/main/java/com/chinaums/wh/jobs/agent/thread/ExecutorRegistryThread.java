package com.chinaums.wh.jobs.agent.thread;

import com.chinaums.wh.job.model.RegistryParam;
import com.chinaums.wh.model.ReturnT;
import com.chinaums.wh.jobs.agent.config.RegistryConfig;
import com.chinaums.wh.jobs.agent.service.JobServiceReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ExecutorRegistryThread {

    @Autowired
    private JobServiceReference jobServiceReference;

    @Value("jobs.agent.appName:DEFAULT")
    private String appName;

    @Value("jobs.agent.address:NONE")
    private String address;

    private Thread registryThread;
    private volatile boolean toStop = false;
    @PostConstruct
    public void start(){
        // valid
        if (appName==null || appName.trim().length()==0) {
            log.warn(">>>>>>>>>>> xxl-job, executor registry config fail, appName is null.");
            return;
        }

//        if (XxlJobExecutor.getAdminBizList() == null) {
//            log.warn(">>>>>>>>>>> xxl-job, executor registry config fail, adminAddresses is null.");
//            return;
//        }return

        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // registry
                while (!toStop) {
                    try {
                        RegistryParam registryParam = null;
                        try {
                            registryParam = new RegistryParam("DEFAULT", appName, address);
                            ReturnT<String> registryResult = jobServiceReference.getJobMngFacade().registryAgent(registryParam);
                            if (registryResult!=null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                                registryResult = ReturnT.SUCCESS;
                                log.debug(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                break;
                            } else {
                                log.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            }
                        } catch (Exception e) {
                            log.info(">>>>>>>>>>> xxl-job registry error, registryParam:{}", registryParam, e);
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    try {
                        if (!toStop) {
                            TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                        }
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            log.warn(">>>>>>>>>>> xxl-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                        }
                    }
                }
            }
        });
        registryThread.setDaemon(true);
        registryThread.setName("xxl-job, executor ExecutorRegistryThread");
        registryThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        registryThread.interrupt();
        try {
            registryThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}

