package me.izhong.jobs.agent.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateUtil {

    /**
     * 两个时间之差
     * @param startDate
     * @param endDate
     * @return 天
     */
    public static long getBetweenDays(Date startDate, Date endDate) {
        return getBetweenHours(startDate,endDate) / 24;
    }

    /**
     * 两个时间之差
     * @param startDate
     * @param endDate
     * @return 小时
     */
    public static long getBetweenHours(Date startDate, Date endDate) {
        return getBetweenMinutes(startDate,endDate) / 60;
    }

    /**
     * 两个时间之差
     * @param startDate
     * @param endDate
     * @return 分钟
     */
    public static long getBetweenMinutes(Date startDate, Date endDate) {
        return getBetweenSecond(startDate,endDate) / 60;
    }

    /**
     * 两个时间只差
     * @param startDate
     * @param endDate
     * @return 秒数
     */
    public static long getBetweenSecond(Date startDate, Date endDate) {
        long seconds = 0;
        try {
            if(startDate!=null&&endDate!=null) {
                long ss = endDate.getTime() - startDate.getTime();
                seconds = ss/1000 ;
            }
        } catch (Exception e) {
            log.error("",e);
        }
        return seconds;
    }

    public static String dateToString(Date date, String dateFormat) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return format.format(date);
        } catch (Exception e) {
            log.error("",e);
            return null;
        }
    }
}
