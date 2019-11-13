package me.izhong.dashboard.web.service;


import me.izhong.dashboard.manage.entity.SysDictData;
import me.izhong.dashboard.manage.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dict")
public class DictService {
    @Autowired
    private SysDictDataService sysDictDataService;

    public List<SysDictData> getType(String dictType) {
        return sysDictDataService.selectNormalDictDataByType(dictType);
    }

    public String getLabel(String dictType, String dictValue) {
        return sysDictDataService.selectDictLabel(dictType, dictValue);
    }
}
