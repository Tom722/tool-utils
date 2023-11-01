package com.scc.toolutils.es.utils;

import com.alibaba.fastjson.JSONObject;
import com.scc.toolutils.es.data.EsField;
import com.scc.toolutils.es.data.EsPage;
import com.scc.toolutils.es.data.TimeQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : scc
 * @date : 2023/01/05
 **/
@Component
@Slf4j
public class ElasticsearchUtils {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${es.index-pre}")
    private String INDEX_PREFIX;
    /**
     * 获取指定索引下记录数量
     */
    public long getMatchCount(List<String> indexList) {
        long count = 0;
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if(CollectionUtils.isEmpty(indexList)){
                return count;
            }
            String[] indexArray = new String[indexList.size()];
            indexList.toArray(indexArray);
            CountRequest countRequest = new CountRequest(indexArray,searchSourceBuilder);
            countRequest.source(searchSourceBuilder);
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            count = countResponse.getCount();
        } catch (Exception e) {
            log.error("获取记录数量异常",e);
        }
        log.info("matched count {}", count);
        return count;
    }

    /**
     * 创建数据
     *
     */
    public boolean addData(String indexName, String jsonString) {
        Assert.notNull(jsonString, "Elasticsearch exception data null");
        Assert.hasLength(indexName, "Elasticsearch exception indexName null");
        IndexResponse indexResponse = null;
        try {
            //准备文档
            Map jsonMap = JSONObject.parseObject(jsonString, Map.class);
            //创建请求
            IndexRequest indexRequest = new IndexRequest(indexName).id(UUID.randomUUID().toString());
            //指定文档内容
            indexRequest.source(jsonMap);
            //true 当存在相同的_id时，插入会出现异常； false 当存在相同_id时，插入会进行覆盖；
            indexRequest.create(true);
            indexRequest.type("_doc");
            //通过client进行http请求
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch addOrUpdateDoc error , meassage = {}", e.getMessage());
        }
        return indexResponse.getResult().equals(DocWriteResponse.Result.CREATED);
    }

    /**
     * 根据id查询文档
     */
    public JSONObject selectDataById(String indexName, String id) {
        Assert.hasLength(id, "Elasticsearch exception id null");
        Assert.notNull(indexName, "Elasticsearch exception indexName null");
        GetResponse response = null;
        try {
            //设置查询的索引、文档
            GetRequest indexRequest = new GetRequest(indexName, "_doc", id);
            response = restHighLevelClient.get(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch selectDataById error , meassage = {}", e.getMessage());
            return null;
        }
        return JSONObject.parseObject(response.getSourceAsString());
    }

    /**
     * 条件查询-根据排序字段进行分页的查询操作
     *
     * @return
     */
    public EsPage<JSONObject> selectDataPageWithTimeQuery(Integer pageNum, Integer pageSize, List<EsField> conditionFields, String[] includeFields, String[] excludeFields, TimeQuery timeQuery) {
        List<JSONObject> res = null;
        // 总记录数
        int total = 0;
        try {
            List<String> indexList = preFixIndexList(timeQuery);
            if(CollectionUtils.isEmpty(indexList)){
                return new EsPage<>(pageNum, pageSize, total, res);
            }
            String[] indexArray = new String[indexList.size()];
            indexList.toArray(indexArray);
            // 构建升降序
            SearchSourceBuilder searchSourceBuilder = buildOrderBuilder(conditionFields);
            // 构建查询条件
            searchSourceBuilder.query(buildBoolMustQueryBuilder(conditionFields));
            // 设置分页
            searchSourceBuilder.from((pageNum - 1) * pageSize).size(pageSize);
            // 获取的字段（列）和不需要获取的列
            searchSourceBuilder.fetchSource(includeFields, excludeFields);
            // 创建检索请求
            SearchRequest searchRequest = new SearchRequest(indexArray, searchSourceBuilder);

            log.info("es 查询条件：{}", searchSourceBuilder.toString());

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //分析结果
            SearchHit[] hits = searchResponse.getHits().getHits();
            total = new Long(searchResponse.getHits().getTotalHits()).intValue();
            res = new ArrayList<>();
            for (SearchHit hit : hits
            ) {
                String id = hit.getId();
                String data = hit.getSourceAsString();
                JSONObject jsonData= JSONObject.parseObject(data);
                jsonData.put("id", id);
                log.info("es查询到：data={}", jsonData);
                res.add(jsonData);
            }
        } catch (Exception e) {
            log.error("elasticsearch selectDataListsByPreciseQuery error , message: ", e);
            throw new RuntimeException("elasticsearch selectDataList error , message: " + e.getMessage());
        }
        return new EsPage<>(pageNum, pageSize, total, res);
    }

    /**
     * 条件查询-查询每个小时的日志数据
     *
     * @return
     */
    public Long queryLogNumPerHour(String indexName, List<EsField> conditionFields) {
        // 总记录数
        Long total = 0L;
        try {
            // 构建升降序
            SearchSourceBuilder searchSourceBuilder = buildOrderBuilder(conditionFields);
            // 构建查询条件
            searchSourceBuilder.query(buildBoolMustQueryBuilder(conditionFields));
            // 设置分页
            searchSourceBuilder.from(1).size(1);

            // 创建检索请求
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);

            log.info("es 查询条件：{}", searchSourceBuilder.toString());

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            total = searchResponse.getHits().getTotalHits();
        } catch (Exception e) {
            log.error("elasticsearch selectDataListsByPreciseQuery error , message: ", e);
            throw new RuntimeException("elasticsearch selectDataList error , message: " + e.getMessage());
        }
        return total;
    }

    /**
     * 条件查询-根据排序字段进行全量查询操作
     *
     * @return
     */
    public List<JSONObject> selectAllDataWithIndexName(List<String> indexList, List<EsField> conditionFields) {
        List<JSONObject> res = null;
        try {
            Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            String[] indexArray = new String[indexList.size()];
            indexList.toArray(indexArray);

            // 创建检索请求
            SearchRequest searchRequest = new SearchRequest(indexArray);
            searchRequest.scroll(scroll);
            // 构建升降序
            SearchSourceBuilder searchSourceBuilder = buildOrderBuilder(conditionFields);
            // 构建查询条件
            searchSourceBuilder.query(buildBoolMustQueryBuilder(conditionFields));
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();

            res = new ArrayList<>();
            while (searchHits != null && searchHits.length > 0) {
                // 处理返回的数据...
                for (SearchHit hit : searchHits
                ) {
                    String data = hit.getSourceAsString();
                    JSONObject jsonData= JSONObject.parseObject(data);
                    //log.info("es查询到：data={}", jsonData);
                    res.add(jsonData);
                }

                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
            }

            // 清理滚动上下文
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("elasticsearch selectAllDataWithIndexName error , message: ", e);
            throw new RuntimeException("elasticsearch selectDataList error , message: " + e.getMessage());
        }
        return res;
    }

    /**
     * 聚合查询
     *
     * @return
     */
    public Map<String,Long> aggregationWithIndexName(String indexName, String aggregationField, int topN) {
        Map<String,Long> res = new HashMap<>();
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.aggregation(AggregationBuilders.terms("group_by_"+aggregationField).field(aggregationField)
                    .order(BucketOrder.count(false)).size(topN));
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_"+aggregationField);
            for (Terms.Bucket entry : terms.getBuckets()) {
                res.put(entry.getKeyAsString(), Long.valueOf(entry.getDocCount()));
            }
        } catch (Exception e) {
            log.error("elasticsearch aggregationWithIndexName error , message: ", e);
            throw new RuntimeException("elasticsearch selectDataList error , message: " + e.getMessage());
        }
        return res;
    }

    /**
     * 聚合查询
     *
     * @return
     */
    public Map<String,Long> aggregationWithIndexNameAndCondition(String indexName, String aggregationField, List<EsField> conditionFields, int topN) {
        Map<String,Long> res = new HashMap<>();
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            // 构建升降序
            SearchSourceBuilder sourceBuilder = buildOrderBuilder(conditionFields);
            // 构建查询条件
            sourceBuilder.query(buildBoolMustQueryBuilder(conditionFields));
            sourceBuilder.aggregation(AggregationBuilders.terms("group_by_"+aggregationField).field(aggregationField)
                    .order(BucketOrder.count(false)).size(topN));
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_"+aggregationField);
            for (Terms.Bucket entry : terms.getBuckets()) {
                res.put(entry.getKeyAsString(), Long.valueOf(entry.getDocCount()));
            }
        } catch (Exception e) {
            log.error("elasticsearch aggregationWithIndexName error , message: ", e);
            throw new RuntimeException("elasticsearch selectDataList error , message: " + e.getMessage());
        }
        return res;
    }

    private static SearchSourceBuilder buildOrderBuilder(List<EsField> conditionFields){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建条件
        if (!CollectionUtils.isEmpty(conditionFields)) {
            for (EsField condFiled :
                    conditionFields) {
                switch (condFiled.getFieldTypeEnum()) {
                    case ORDER_ASC:
                        searchSourceBuilder.sort(condFiled.getField(), SortOrder.ASC);
                        break;
                    case ORDER_ESC:
                        searchSourceBuilder.sort(condFiled.getField(), SortOrder.DESC);
                        break;
                    default:
                        break;
                }
            }
        }
        return searchSourceBuilder;
    }

    private static QueryBuilder buildBoolMustQueryBuilder(List<EsField> conditionFields){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构建条件
        if (!CollectionUtils.isEmpty(conditionFields)) {
            for (EsField condFiled :
                    conditionFields) {
                switch (condFiled.getFieldTypeEnum()) {
                    case VAGUE_QUERY:
                        // 模糊查询
                        boolQueryBuilder.must(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()).fuzziness(Fuzziness.AUTO));
                        break;
                    case PRECISE_QUERY:
                        // 精准查询
                        boolQueryBuilder.must(QueryBuilders.matchQuery(condFiled.getField()+".keyword", condFiled.getValue()));
                        break;
                    case IN_LONG:
                        Long[] value =(Long[]) condFiled.getValue();
                        // 范围查询
                        boolQueryBuilder.must(QueryBuilders.termsQuery(condFiled.getField(), value));
                        break;
                    case REGEXP_QUERY:
                        boolQueryBuilder.must(QueryBuilders.regexpQuery(condFiled.getField(), (String) condFiled.getValue()));
                        break;
                    case RANGE_QUERY:
                        String startTimeStr = String.valueOf(condFiled.getValue()).split(";")[0];
                        String endTimeStr = String.valueOf(condFiled.getValue()).split(";")[1];
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(condFiled.getField()+".keyword")
                                .gte(startTimeStr)
                                .lte(endTimeStr));
                        break;
                    default:
                        //默认精准查询
                        boolQueryBuilder.must(QueryBuilders.matchQuery(condFiled.getField(), condFiled.getValue()));
                        break;
                }
            }
        }
        return boolQueryBuilder;
    }
    /**
     * 构建起始日期之间的索引列表
     */
    public List<String> buildIndexList(TimeQuery timeQuery) {
        return TimeUtil.getIntervalDays(timeQuery).stream().map(
                item -> String.format("%s%s-*", INDEX_PREFIX, item)).collect(Collectors.toList());
    }
    private static class TimeUtil {
        public static final String DATE_FORMAT = "yyyy_MM_dd";
        public static final String DATE_FORMAT_INDEX = "yyyy-MM-dd";
        public static final String DEFAULT_TZ = "Asia/Shanghai";
        public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private static TimeZone getDefaultTimeZone() {
            return TimeZone.getTimeZone(DEFAULT_TZ);
        }

        private static SimpleDateFormat getDefaultFormat() {
            return getDefaultFormat(DEFAULT_FORMAT);
        }

        private static SimpleDateFormat getDefaultFormat(String format) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(getDefaultTimeZone());
            return sdf;
        }

        private static List<String> getIntervalDays(TimeQuery timeQuery) {
            return getIntervalTimes(timeQuery, DATE_FORMAT_INDEX, Calendar.DAY_OF_YEAR, 1, false)
                    .stream().map(TimeUnit::getDate).collect(Collectors.toList());
        }

        public static String timestampToDate(long timestamp) {
            return getDefaultFormat().format(new Date(timestamp));
        }

        /**
         * 构建时间区间
         *
         * @param query    起始\结束时间
         * @param format   时间格式
         * @param field    区间单位
         * @param interval 区间长度
         */
        private static List<TimeUnit> getIntervalTimes(TimeQuery query, String format, int field, int interval,
                                                       boolean stamp) {
            List<TimeUnit> values = new ArrayList<>();
            if (query.getStart() == null || query.getEnd() == null) {
                log.error("invalid time start or end {}", query);
                return values;
            }

            DateFormat dateFormat = getDefaultFormat(format);
            try {
                TimeZone zone = getDefaultTimeZone();
                Calendar tempStart = Calendar.getInstance(zone);
                tempStart.setTime(query.getStart());

                Calendar tempEnd = Calendar.getInstance(zone);
                tempEnd.setTime(query.getEnd());
//                tempEnd.add(Calendar.DATE, +1);

                while (tempStart.before(tempEnd)||tempStart.equals(tempEnd)) {
                    values.add(new TimeUnit(dateFormat.format(tempStart.getTime()), stamp ?
                            tempStart.getTime().getTime() : 0L));
                    tempStart.add(field, interval);
                }
            } catch (Exception e) {
                log.error("context",e);
            }

            return values;
        }
    }
    @Data
    private static class TimeUnit {
        private String date;
        private long timeStamp;
        private boolean showXAxis;

        public TimeUnit(String date, long timeStamp) {
            this.date = date;
            this.timeStamp = timeStamp;
        }
    }

    /**
     * 构建索引列表
     */
    public List<String> preFixIndexList(TimeQuery timeQuery) {
        List<String> result = new ArrayList<>();
        boolean flag;
        GetIndexRequest getIndexRequest;
        try {
            if(timeQuery == null || !timeQuery.isValid()){
                result.add(INDEX_PREFIX+"*");
            }else{
                result = buildIndexList(timeQuery);
                Iterator<String> iterator = result.iterator();
                while (iterator.hasNext()){
                    String item = iterator.next();
                    getIndexRequest = new GetIndexRequest(item);
                    flag = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
                    if(!flag) iterator.remove();
                }
            }
            log.info("索引列表：{}", result);
        }catch (Exception e){
            log.error("获取index异常：",e);
            return result;
        }
        return result;
    }
}
