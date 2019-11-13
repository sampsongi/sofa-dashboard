package me.izhong.dashboard.manage.service.impl;

import me.izhong.db.common.service.CrudBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.izhong.dashboard.manage.dao.DictDataDao;
import me.izhong.dashboard.manage.entity.SysDictData;
import me.izhong.dashboard.manage.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SysDictDataServiceImpl extends CrudBaseServiceImpl<Long,SysDictData> implements SysDictDataService {

    @Autowired
    private DictDataDao dictDataDao;

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        return dictDataDao.findAllByDictTypeOrderByDictSortAsc(dictType);
    }

    @Override
    public List<SysDictData> selectNormalDictDataByType(String dictType) {
        return dictDataDao.findAllByDictTypeAndStatusOrderByDictSortAsc(dictType,"0");
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        SysDictData sysDictData = dictDataDao.findByDictTypeAndDictValue(dictType, dictValue);
        if (sysDictData != null) {
            return sysDictData.getDictLabel();
        }
        return "";
    }

}
