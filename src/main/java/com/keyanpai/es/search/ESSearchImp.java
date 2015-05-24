package com.keyanpai.es.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.keyanpai.es.MySearchOption;
import com.keyanpai.es.MySearchOption.DataFilter;
import com.keyanpai.es.MySearchOption.SearchLogic;


public class ESSearchImp {
	
	private Client ESClient = null;
	private Logger logger = Logger.getLogger(ESSearchImp.class);	
	public ESSearchImp(Client ESClient){	
		this.ESClient = ESClient;
		PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
	}	
	private ESCreatQueryBuilder esCreatQueryBuilder = new  ESCreatQueryBuilder();
	

	public List<Map<String, Object>> simpleSearch(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap,
			SearchLogic filterLogic, int from, int offset, String sortField,
			String sortType) {
		// TODO Auto-generated method stub
		
		if(offset <= 0){
			return null;
		}
		try{
			QueryBuilder queryBuilder = null;
			queryBuilder = this.esCreatQueryBuilder.createQueryBuilder(searchContentMap,searchLogic);
			queryBuilder = this.createFilterBuilder(filterLogic,queryBuilder,searchContentMap
					,filterContentMap);
			this.ESClient.admin().cluster().prepareClusterStats().execute().actionGet();
			SearchRequestBuilder searchRequestBuilder = this.ESClient.prepareSearch(indexNames)
					.setSearchType(SearchType.DEFAULT).setQuery( queryBuilder).setFrom(from)
					.setSize(offset).setExplain(true);
			 if (sortField == null || sortField.isEmpty() 
					 || sortType == null || sortType.isEmpty()) {
				 /* 不需要排序*/
				 }
			 else {
				 /*需要排序*/
				 org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? 
						 org.elasticsearch.search.sort.SortOrder.DESC 
						 : org.elasticsearch.search.sort.SortOrder.ASC;	 
				 searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
			 }
//			 searchRequestBuilder = this.createHighlight(searchRequestBuilder, searchContentMap);
			 this.logger.debug(searchRequestBuilder.toString());
			 SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			
			 return this.getSearchResult(searchResponse);
		}
		catch(Exception e)
    	{
    		 this.logger.error(e.getMessage());
    	}
		
		return null;
	}
	
	private List<Map<String, Object>> getSearchResult(
			SearchResponse searchResponse) {
		// TODO Auto-generated method stub
		try{
			List<Map<String,Object> > resultList = new ArrayList<Map<String ,Object> >();
			for(SearchHit searchHit : searchResponse.getHits()){
				Iterator<Entry<String, Object>> iterator = searchHit.getSource()
						.entrySet().iterator();
				HashMap<String,Object> resultMap = new HashMap<String,Object>();
				while(iterator.hasNext()){
					Entry<String, Object> entry = iterator.next();
					resultMap.put(entry.getKey(), entry.getValue());
				}
//				 Map<String, HighlightField> highlightMap = searchHit.highlightFields();
//				 Iterator<Entry<String, HighlightField>> highlightIterator = highlightMap
//						 .entrySet().iterator();
//				while (highlightIterator.hasNext()) {
//					   Entry<String, HighlightField> entry = highlightIterator.next();
//					   Object[] contents = entry.getValue().fragments();
//					   if (contents.length == 1) {
//						   resultMap.put(entry.getKey(), contents[0].toString());
//						   System.out.println(contents[0].toString());						   
//					   }
//					   else {
//						   this.logger.warn("搜索结果中的高亮结果出现多数据contents.length = " + contents.length);
//					   }
//				}
				  System.out.println(resultMap.toString());		
				resultList.add(resultMap);
			}
		 return resultList;
		}
		catch(Exception e){
			 this.logger.error(e.getMessage());
		 }
		return null;
	}

	private QueryBuilder createFilterBuilder(SearchLogic filterLogic,
			QueryBuilder queryBuilder,
			HashMap<String, Object[]> searchContentMap,
			HashMap<String, Object[]> filterContentMap) {
		// TODO Auto-generated method stub
		try{
			
			Iterator<Entry<String,Object[]>> iterator = searchContentMap.entrySet().iterator();
			AndFilterBuilder andFilterBuilder = null;
			while(iterator.hasNext()){
				Entry<String,Object[]> entry =iterator.next();
				  Object[] values = entry.getValue();
				  if (!this.checkValue(values)) {
					  continue;
				  }
				  MySearchOption mySearchOption = this.getSearchOption(values);
				  if (mySearchOption.getDataFilter() == DataFilter.exists) {
					  /*被搜索的条件必须有值，exists过滤条件*/
					  ExistsFilterBuilder existsFilterBuilder = FilterBuilders.existsFilter(entry.getKey());
					  if (andFilterBuilder == null) {
						  andFilterBuilder = FilterBuilders.andFilter(existsFilterBuilder);
					  }
					  else {
						  andFilterBuilder = andFilterBuilder.add(existsFilterBuilder);
					  }
				  }
			}
		   if (filterContentMap == null || filterContentMap.isEmpty()) {
			 /*如果没有其它过滤条件，返回*/					
			 return QueryBuilders.filteredQuery(queryBuilder, andFilterBuilder);
		   }
		     /*构造过滤条件*/
			//System.out.println("filterContentMap != null");
			QueryFilterBuilder queryFilterBuilder = FilterBuilders				
					.queryFilter(this.esCreatQueryBuilder.createQueryBuilder(filterContentMap, filterLogic));
			/*构造not过滤条件，表示搜索结果不包含这些内容，而不是不过滤*/
		    NotFilterBuilder notFilterBuilder = FilterBuilders.notFilter(queryFilterBuilder);
			return QueryBuilders.filteredQuery(
						  queryBuilder, FilterBuilders.andFilter(andFilterBuilder, notFilterBuilder));
//			return QueryBuilders.filteredQuery(
//						  queryBuilder, FilterBuilders.andFilter(andFilterBuilder,queryFilterBuilder));
			
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
		
		
		return null;
	}

	private MySearchOption getSearchOption(Object[] values) {
		// TODO Auto-generated method stub
		try{
			 for(Object item : values) {
				 if(item instanceof MySearchOption) {
					 return (MySearchOption) item;
				 }
			 }
		 }
		 catch(Exception e){
			 this.logger.error(e.getMessage());
		 }
		 return new MySearchOption();
	}

	private boolean checkValue(Object[] values) {
		// TODO Auto-generated method stub
		if(values == null)
			return false;
		else if(values.length == 0)
			return false;
		else if(values[0] == null)
			return false;
		else if(values[0].toString().trim().isEmpty())
			return false;
	return true;
	}

	public long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		QueryBuilder queryBuilder = null;
		 try {
			 queryBuilder = this.esCreatQueryBuilder.createQueryBuilder(searchContentMap, searchLogic);
			 queryBuilder = this.createFilterBuilder(searchLogic, queryBuilder, searchContentMap, filterContentMap);
			 SearchResponse searchResponse = this.searchCountRequest(indexNames, queryBuilder);
			 return searchResponse.getHits().totalHits();
		 }
		 catch(Exception e)
		 {
			 this.logger.error(e.getMessage());
		 }
		return 0;
		
	}

	private SearchResponse searchCountRequest(String[] indexNames,
			QueryBuilder queryBuilder) {
		// TODO Auto-generated method stub
		try{
		
			SearchRequestBuilder searchRequestBuilder = this.ESClient
					.prepareSearch(indexNames).setSearchType(SearchType.COUNT);
			if (queryBuilder instanceof QueryBuilder) {
				 searchRequestBuilder = searchRequestBuilder.setQuery((QueryBuilder)queryBuilder);
				 this.logger.debug(searchRequestBuilder.toString());
				}
	
			return searchRequestBuilder.execute().actionGet();
		 }
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return null;
		
}

	public List<Map<String, Object>> getSuggest(String[] indexNames,
			String filedName, String value, int count) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	 private Map<String, String> _group(String indexName, QueryBuilder queryBuilder, String[] groupFields) {
	        try {
	            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("group").fields(groupFields).size(9999);
	            SearchRequestBuilder searchRequestBuilder = this.ESClient.prepareSearch(indexName).setSearchType(SearchType.DEFAULT)
	                    .addFacet(termsFacetBuilder).setQuery(queryBuilder).setFrom(0).setSize(1).setExplain(true);
	            this.logger.debug(searchRequestBuilder.toString());
	            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
	            TermsFacet termsFacet = searchResponse.getFacets().facet("group");
	            HashMap<String, String> result = new HashMap<String, String>();
	            for (org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : termsFacet.getEntries()) {
	                result.put(entry.getTerm()+"", entry.getCount()+"");
	            }
	            return result;
	        }
	        catch (Exception e) {
	            this.logger.error(e.getMessage());
	        }
	        return null;
	    }

	    public Map<String, String> group(String indexName
	            , HashMap<String, Object[]> mustSearchContentMap
	            , HashMap<String, Object[]> shouldSearchContentMap
	            , String[] groupFields) {
	        /*创建must搜索条件*/
	        QueryBuilder mustQueryBuilder = this.esCreatQueryBuilder.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
	        /*创建should搜索条件*/
	        QueryBuilder shouldQueryBuilder = this.esCreatQueryBuilder.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
	        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
	            return null;
	        }
	        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
	        if (mustQueryBuilder != null) {
	            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
	        }
	        if (shouldQueryBuilder != null) {
	            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
	        }
	        try {
	            return this._group(indexName, boolQueryBuilder, groupFields);
	        }
	        catch (Exception e) {
	            this.logger.error(e.getMessage());
	        }
	        return null;
	    }

	

}
