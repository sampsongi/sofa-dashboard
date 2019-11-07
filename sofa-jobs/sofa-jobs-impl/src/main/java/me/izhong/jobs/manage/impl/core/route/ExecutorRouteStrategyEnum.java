package me.izhong.jobs.manage.impl.core.route;

import me.izhong.jobs.manage.impl.core.route.strategy.*;

public enum ExecutorRouteStrategyEnum {

    FIRST("第一个", new ExecutorRouteFirst()),
    LAST("最后一个", new ExecutorRouteLast()),
    ROUND("轮训", new ExecutorRouteRound()),
    RANDOM("随机", new ExecutorRouteRandom()),
    CONSISTENT_HASH("HASH", new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED("LFU", new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED("LRU", new ExecutorRouteLRU()),
    FAILOVER("FAILOVER", new ExecutorRouteFailover()),
    BUSYOVER("BUSYOVER", new ExecutorRouteBusyover()),
    SHARDING_BROADCAST("SHARDING", null);

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    private String title;
    private ExecutorRouter router;

    public String getTitle() {
        return title;
    }
    public ExecutorRouter getRouter() {
        return router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
