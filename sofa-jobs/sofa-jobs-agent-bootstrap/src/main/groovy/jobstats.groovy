import me.izhong.jobs.agent.util.JobStatsUtils

try {

    HashMap<String,String> params = params;
    log.info("参数是:{} {}",params,params.get("xx"));

    String key ="xxx.so"

    boolean exist = JobStatsUtils.checkExist(key);
    println("exist:${exist}")

    String value1 = JobStatsUtils.getValue1(key)
    println("value1:${value1}")

    JobStatsUtils.insertOrUpdate(key,"ty","耗时2");

    value1 = JobStatsUtils.getValue1(key)
    println("update value1:${value1}")


    return 0
} catch (Exception e) {
    log.error("", e);
    return -1;
}


