package me.izhong.dashboard.manage.service;

import me.izhong.dashboard.manage.domain.PageModel;
import me.izhong.dashboard.manage.domain.PageRequest;
import me.izhong.dashboard.manage.entity.SysPost;
import me.izhong.dashboard.manage.expection.BusinessException;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface CrudBaseService<K,T> {

     PageModel<T> selectPage(Query query, PageRequest pageRequest, T searchUser);

     PageModel<T> selectPage(PageRequest request, T target);

     List<T> selectList(Query query, PageRequest pageRequest, T searchUser);

     List<T> selectList(PageRequest request, T target);

     List<T> selectList(T target);

     long count(Query query, PageRequest pageRequest, T searchUser);

     long count(PageRequest request, T target);

     long count();

     T selectByPId(K pId);

     List<T> selectAll();

     long deleteByPId(K pId) throws BusinessException;

     long deleteByPIds(String pIds) throws BusinessException;

     long deleteAll();

     long clearAll();

     T insert(T target);

     T update(T target);

     long remove(K pId);

     long remove(List<K> pIds);

}
