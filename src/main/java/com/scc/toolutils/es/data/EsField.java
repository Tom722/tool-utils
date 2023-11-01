package com.scc.toolutils.es.data;

import com.scc.toolutils.es.enums.FieldTypeEnum;
import lombok.Data;

/**
 * @author : scc
 * @date : 2023/01/05
 **/
@Data
public class EsField {
    //字段对应操作功能
    private FieldTypeEnum fieldTypeEnum;
    //查询字段名称
    private String field;
    //查询字段对应的值
    private Object value;
    public EsField(){

    }
    public EsField(String field ,Object value , FieldTypeEnum fieldTypeEnum){
        this.value = value;
        this.field = field;
        this.fieldTypeEnum  = fieldTypeEnum;
    }
}