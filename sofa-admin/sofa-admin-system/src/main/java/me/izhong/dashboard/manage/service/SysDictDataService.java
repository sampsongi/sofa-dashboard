package me.izhong.dashboard.manage.service;

import me.izhong.dashboard.manage.entity.SysDictData;
import me.izhong.dashboard.manage.service.impl.CrudBaseServiceImpl;

import java.util.List;

public interface SysDictDataService extends CrudBaseService<Long,SysDictData> {

    List<SysDictData> selectDictDataByType(String dictType);

    String selectDictLabel(String dictType, String dictValue);

}
