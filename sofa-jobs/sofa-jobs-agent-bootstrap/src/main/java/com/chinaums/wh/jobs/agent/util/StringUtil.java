package com.chinaums.wh.jobs.agent.util;

import java.util.List;

public class StringUtil {

    public static String firstValue(List<String> list) {
        if(list == null || list.size() <= 0)
            return null;
        return list.get(0);
    }
}
