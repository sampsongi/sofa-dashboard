package com.chinaums.wh.db.common.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.chinaums.wh.db.common.domain.MongoLock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class MongoLockDao {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * 返回指定key的数据
     *
     * @param key
     * @return
     */
    public List<MongoLock> getByKey(String key) {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        return (List<MongoLock>) mongoTemplate.find(query, MongoLock.class);
    }


    /**
     * 指定key自增increment(原子加),并设置过期时间
     *
     * @param key
     * @param increment
     * @param expire
     * @return
     */
    public Map<String, Object> incrByWithExpire(String key, double increment, long expire) {
        //筛选
        Query query = new Query();
        query.addCriteria(new Criteria("key").is(key));

        //更新
        Update update = new Update();
        update.inc("value", increment);
        update.set("expire", expire);
        //可选项
        FindAndModifyOptions options = FindAndModifyOptions.options();
        //没有则新增
        options.upsert(true);
        //返回更新后的值
        options.returnNew(true);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("value", Double.valueOf(((MongoLock)
                mongoTemplate.findAndModify(query, update, options, MongoLock.class)).getValue()).intValue());
        resultMap.put("expire", Long.valueOf(((MongoLock)
                mongoTemplate.findAndModify(query, update, options, MongoLock.class)).getExpire()).longValue());
        return resultMap;
    }


    /**
     * 根据value删除过期的内容
     *
     * @param key
     * @param expireTime
     */
    public void removeExpire(String key, long expireTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        query.addCriteria(Criteria.where("expire").lt(expireTime));
        mongoTemplate.remove(query, MongoLock.class);
    }

    public void remove(Map<String, Object> condition) {
        Query query = new Query();
        Set<Map.Entry<String, Object>> set = condition.entrySet();
        int flag = 0;
        for (Map.Entry<String, Object> entry : set) {
            query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
            flag = flag + 1;
        }
        if (flag == 0) {
            query = null;
        }
        mongoTemplate.remove(query,MongoLock.class);
    }

}