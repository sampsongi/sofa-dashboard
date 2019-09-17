package com.chinaums.wh.db.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageModel<T> implements Serializable {

    private Long count;
    private List<T> rows;

    public PageModel() {

    }

    public PageModel(Long count, List<T> rows) {
        this.count = count;
        this.rows = rows;
    }

    public static <T> PageModel<T> instance(Long count, List<T> rows) {
        return new PageModel<T>(count, rows);
    }

    public static <T> PageModel<T> instance(Integer count, List<T> rows) {
        return new PageModel<T>(count.longValue(), rows);
    }

}
