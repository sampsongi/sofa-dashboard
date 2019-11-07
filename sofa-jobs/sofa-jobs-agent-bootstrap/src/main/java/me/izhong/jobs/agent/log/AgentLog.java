package me.izhong.jobs.agent.log;

public interface AgentLog {

    void info(String s);

    void info(String s, Object... args);

    void debug(String s);
}
