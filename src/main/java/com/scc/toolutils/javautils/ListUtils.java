package com.scc.toolutils.javautils;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author : scc
 * @date : 2023/04/28
 **/
public class ListUtils {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        //分成11组，每组个数不确定
        List<List<Integer>> group = group(list, 11);
        System.out.println(group);
        // 每组最大11个，分成几组不确定
        List<List<Integer>> lists = splitList(list, 11);
        System.out.println(lists);
    }
    /**
     * 集合按照指定个数进行分组
     * 把集合平均分散到每个分组中，集合按照numGroup进行分组，分组个数<=numGroup
     *
     * @param list
     * @param numGroup
     * @return 分组后的集合
     */
    public static List<List<Integer>> group(List<Integer> list, int numGroup) {
        if (list == null || list.isEmpty() || numGroup < 1) {
            return Collections.emptyList();
        }
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

    /**
     * 按指定大小，分隔集合; 将集合按【规定个数】分为n个部分
     * @param <T>
     * @param list
     * @param len
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {

        if (list == null || list.isEmpty() || len < 1) {
            return Collections.emptyList();
        }

        List<List<T>> result = new ArrayList<>();

        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }

        return result;
    }

    /**
     * 使用guava方法来实现：将集合按【规定个数】分为n个部分
     * @param list
     * @param len
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitListTwo(List<T> list, int len) {
        List<List<T>> partition = Lists.partition(list , len);
        return partition;
    }
}
