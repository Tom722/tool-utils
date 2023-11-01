package com.scc.toolutils.es.enums;

/**
 * @author : scc
 * @date : 2023/01/05
 **/
public enum FieldTypeEnum {
    //精准查询
    PRECISE_QUERY,
    //模糊查询
    VAGUE_QUERY,
    //排序_升序
    ORDER_ASC,
    //排序_降序
    ORDER_ESC,
    //时间范围查询
    TS_RANGE_QUERY,
    RANGE_QUERY,
    IN_LONG,
    REGEXP_QUERY
    ;
}
