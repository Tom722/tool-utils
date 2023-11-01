package com.scc.toolutils.es.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : scc
 * @date : 2023/01/10
 **/
@Data
@AllArgsConstructor
public class EsSearchData {
    private String userId;
    private String inKeyword;
    private String outKeyword;
    private String timeQuery;
}
