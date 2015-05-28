package com.keyanpai.dao.esStats;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;

public abstract class ESStats {	
	abstract double getMin(String fieldName);
	abstract double getMax(String fieldName);
	abstract double getAvg(String fieldName);
	abstract double getSum(String fieldName);
	abstract long getCount(String fieldName);
	public abstract Stats getStats(String fieldName);
	public abstract SearchResponse getStatByCondition(String indexName,String by_term,String by_time,String by_agg,MetricsAggregationBuilder<?> agg );
}
