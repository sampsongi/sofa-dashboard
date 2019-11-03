package com.chinaums.wh.jobs.agent.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleLog implements AgentLog {
    @Override
    public void info(String s) {
        log.info(s);
    }

    @Override
    public void info(String s, Object... args) {
        log.info(s, args);
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }


}
