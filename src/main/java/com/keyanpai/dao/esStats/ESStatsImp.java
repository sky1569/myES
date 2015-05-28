package com.keyanpai.dao.esStats;

import keyanpai.ResourceLoader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;


public class ESStatsImp extends ESStats{
	private Client ESClient = null;	
	private Logger logger = Logger.getLogger(ESStatsImp.class);
	
	
	public ESStatsImp(Client esClient)	{
		this.ESClient = esClient;	
		//PropertyConfigurator.configure(ResourceLoader.loadLog4jProperties("log4j.properties")) ;
	}	

	public long getCount(String fieldName)
	{
		try{
			SearchResponse sr = this.ESClient.prepareSearch()
					//.setQuery(queryBuilder)
					.addAggregation(
							AggregationBuilders.stats("by_count").field(fieldName)
							).execute().actionGet();
			Stats agg = sr.getAggregations().get("by_count");
			return agg.getCount();
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}		
		return 0;
	}
	
	public double getMin(String fieldName) {
		// TODO Auto-generated method stub
		try{
				SearchResponse sr = this.ESClient.prepareSearch()
						.addAggregation(
									AggregationBuilders.min("by_min").field(fieldName))
									.execute().actionGet();		
				Min min = sr.getAggregations().get("by_min");		
				return min.getValue();
			}
			catch(Exception e){
				this.logger.error(e.getMessage());
			}		
			return 0;
	}
	
	public double getMax(String fieldName) {
		// TODO Auto-generated method stub
		try{
			SearchResponse sr = this.ESClient.prepareSearch()
					.addAggregation(
								AggregationBuilders.max("by_max").field(fieldName))
								.execute().actionGet();		
			Min max = sr.getAggregations().get("by_max");		
			return max.getValue();
		}catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}		
		return 0;
	}
	
	public double getAvg(String fieldName){
		// TODO Auto-generated method stub
		try{
			SearchResponse sr = this.ESClient.prepareSearch()
					.addAggregation(
								AggregationBuilders.avg("by_avg").field(fieldName))
								.execute().actionGet();		
			Avg avg = sr.getAggregations().get("by_avg");		
			return avg.getValue();
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}		
		return 0;
	}
	
	public double getSum(String fieldName){
		// TODO Auto-generated method stub
		try{
			SearchResponse sr = this.ESClient.prepareSearch()
					.addAggregation(
								AggregationBuilders.sum("by_sum").field(fieldName))
								.execute().actionGet();		
			Sum sum = sr.getAggregations().get("by_sum");		
			return sum.getValue();
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}		
		return 0;
	}
	
	public Stats getStats(String fieldName)
	{
		// TODO Auto-generated method stub
		try{
				SearchResponse sr = this.ESClient.prepareSearch()
						.addAggregation(
									AggregationBuilders.avg("by_stats").field(fieldName))
									.execute().actionGet();		
				return sr.getAggregations().get("by_stats");
				
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}		
		return null;
	}

	
	
	@Override
	//!!!!!!!不懂
	public SearchResponse getStatByCondition(String indexName, String by_term,
			String by_time, String by_agg, MetricsAggregationBuilder<?> agg) {
		// TODO Auto-generated method stub
		try{
			SearchResponse sr = this.ESClient.prepareSearch(indexName)
				    .addAggregation(
				        AggregationBuilders.terms("by_term").field(by_term)
				        .subAggregation(AggregationBuilders.dateHistogram("by_time")
				            .field(by_time)
				            .interval((DateHistogram.Interval.YEAR))
				            .subAggregation(agg)
				        )
				    )
				    .execute().actionGet();
			return sr;
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}		
		return null;
	}




}
