package me.izhong.jobs.agent.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleLog implements AgentLog {
    public static final String PRIFIX = "A:";
    @Override
    public void info(String s) {
        log.info(PRIFIX  + s);
    }

    @Override
    public void info(String s, Object... args) {
        log.info(PRIFIX  +s, args);
    }

    @Override
    public void debug(String s) {
        log.debug(PRIFIX  +s);
    }


}
