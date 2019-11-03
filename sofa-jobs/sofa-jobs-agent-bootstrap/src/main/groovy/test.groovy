
import org.apache.commons.lang3.time.DateUtils


try {

    Date yesterday = DateUtils.addDays(new Date(), -1);
    println "脚本操作异常，错误信息:" + yesterday

    return 0
} catch (Exception e) {
    log.error("", e);
    return -1;
}


