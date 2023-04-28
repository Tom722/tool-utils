package com.scc.toolutils.javautils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : scc
 * @date : 2023/04/28
 **/
public class ListUtils {

    /**
     * 集合按照指定个数进行分组
     * 把集合平均分散到每个分组中，集合按照numGroup进行分组，分组个数<=numGroup
     *
     * @param list
     * @param numGroup
     * @return 分组后的集合
     */
    public static List<List<Integer>> group(List<Integer> list, int numGroup) {
        int size = list.size();
        int groups = Math.min(numGroup, size);
        int groupSize = (int) Math.ceil((double) size / groups);
        List<List<Integer>> result = new ArrayList<>();
        int i = 0;
        while (i < size) {
            List<Integer> group = new ArrayList<>();
            for (int j = 0; j < groupSize && i < size; j++) {
                group.add(list.get(i));
                i++;
            }
            result.add(group);
        }
        return result;
    }
}
