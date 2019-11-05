package com.chinaums.wh.jobs.agent.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StringUtil {

    public static String firstValue(List<String> list) {
        if(list == null || list.size() <= 0)
            return null;
        return list.get(0);
    }


    public static Map<String,String> parseParams(String list) {
        if(list == null || list.trim().length() <= 1)
            return null;
        list =list.trim();
        if(list.charAt(0) != '{') {
            return null;
        }
        Map<String,String> results = new HashMap<>();
        list = list.substring(1,list.length()-1);
        log.info("list2:{}",list);
        String[] kvs = list.split(",");
        for(String kv :kvs){
            if(kv.indexOf("=") > 0) {
                String[] tmp = kv.split("=");
                if(tmp.length > 1) {
                    String k = tmp[0];
                    String v = tmp[1];
                    results.put(k,v);
                }
            }
        }
        return results;
    }
}
