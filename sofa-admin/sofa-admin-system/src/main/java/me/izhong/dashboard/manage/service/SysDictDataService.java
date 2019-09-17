package me.izhong.dashboard.manage.service;

import com.chinaums.wh.db.common.service.CrudBaseService;
import me.izhong.dashboard.manage.entity.SysDictData;

import java.util.List;

public interface SysDictDataService extends CrudBaseService<Long,SysDictData> {

    List<SysDictData> selectDictDataByType(String dictType);

    String selectDictLabel(String dictType, String dictValue);

}
