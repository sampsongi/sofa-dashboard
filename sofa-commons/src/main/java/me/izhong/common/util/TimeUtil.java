package me.izhong.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static Date parseDate_yyyyMMdd_hl(String value) {
        if (value == null)
            return null;
        return parseDate("yyyy-MM-dd", value);
    }

    public static Date parseDate(String format, String value) {
        if (StringUtils.isBlank(value))
            return null;
        try {
            return new SimpleDateFormat(format).parse(value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(parseDate_yyyyMMdd_hl("20180101"));
        System.out.println(parseDate_yyyyMMdd_hl("2018-01-01"));
    }
}
