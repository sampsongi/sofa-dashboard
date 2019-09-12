package com.chinaums.wh.jobs.agent.controller;

import com.chinaums.wh.job.model.Job;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 接受任务调度命令，执行任务，停止任务
 */
@Controller
@RequestMapping("/job")
public class CommandReceiverController {

    @RequestMapping("run")
    public void run(Job job, String script){
        //加载groovy ，通过run.sh调用
    }

    @RequestMapping("term")
    public void term(Job job){

    }
}
