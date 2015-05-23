package com.keyanpai.esImp;

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
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.keyanpai.esInterface.ESSearch;
import com.keyanpai.instance.MySearchOption;
import com.keyanpai.instance.MySearchOption.DataFilter;
import com.keyanpai.instance.MySearchOption.SearchLogic;


public class ESSearchImp implements ESSearch{
	
	private Client ESClient = null;
	private Logger logger = Logger.getLogger(ESSearchImp.class);	
	public ESSearchImp(){		
		PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
	}	
	
	public boolean searchConfigure(Client esClient) {
		// TODO Auto-generated method stub
		try{
			this.ESClient = esClient;
			return true;
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
		return false;
	}	
	
	public List<Map<String, Object>> simpleSearch(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			HashMap<String, Object[]> filterContentMap, int from, int offset,
			 String sortField, String sortType) {
		// TODO Auto-generated method stub
		SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
		return this.simpleSearch(
				indexNames, searchContentMap, searchLogic, filterContentMap
				, searchLogic, from, offset, sortField, sortType);
	}
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
			queryBuilder = this.createQueryBuilder(searchContentMap,searchLogic);
			queryBuilder = this.createFilterBuilder(filterLogic,queryBuilder,searchContentMap
					,filterContentMap);
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
			System.out.println("filterContentMap != null");
			QueryFilterBuilder queryFilterBuilder = FilterBuilders				
					.queryFilter(this.createQueryBuilder(filterContentMap, filterLogic));
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

	public QueryBuilder createQueryBuilder(
			HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic) {
		// TODO Auto-generated method stub
		try{
			if(searchContentMap == null || searchContentMap.size() == 0){
				return null;				
			}
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			Iterator<Entry<String,Object[]>> iterator = searchContentMap.entrySet().iterator();
			/*循环每一个需要搜索的字段和值*/
			while(iterator.hasNext()){
				Entry<String,Object[]> entry = iterator.next();
				String field =entry.getKey();			
				Object[] values = entry.getValue();		
				/*排除非法的搜索值*/
				if(!this.checkValue(values)){
					continue;
				}
				MySearchOption mySearchOption = this.getSearchOption(values);			
				
				QueryBuilder queryBuilder = this.createSingleFieldQueryBuilder(
						field, values, mySearchOption);
				if(queryBuilder != null)				{
					if(searchLogic == SearchLogic.should){
						boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
					}
					else{
						boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
					}
				}
			}
			return boolQueryBuilder;
		}
		catch(Exception e){
			 this.logger.error(e.getMessage());
		}
			
		return null;
	}

	private QueryBuilder createSingleFieldQueryBuilder(String field,
			Object[] values, MySearchOption mySearchOption) {
		// TODO Auto-generated method stub
		try{
			if(mySearchOption.getSearchType() 
					== com.keyanpai.instance.MySearchOption.SearchType.range)
				return this.createRangeQueryBuilder(field,values);
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			 for (Object valueItem : values) {
				 if (valueItem instanceof MySearchOption) {
					 continue;
				 }
				 QueryBuilder queryBuilder = null;
				 String formatValue = valueItem.toString().trim().replace("*", "");//格式化搜索数据
		
				 if (mySearchOption.getSearchType() 
						 == com.keyanpai.instance.MySearchOption.SearchType.term) {
					 queryBuilder = QueryBuilders.termQuery(field, formatValue).boost(mySearchOption.getBoost());
				 }
				 else if (mySearchOption.getSearchType() 
						 == com.keyanpai.instance.MySearchOption.SearchType.querystring) {
//					 if (formatValue.length() == 1) {
//						 /*如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，就不需要通配符了*/
//						 if (!Pattern.matches("[0-9]", formatValue)) {
//							 formatValue = "*"+formatValue+"*";							
//						 }
//					 }
					 QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(formatValue)
							 .minimumShouldMatch(mySearchOption.getQueryStringPrecision());
					 queryBuilder = queryStringQueryBuilder.field(field).boost(mySearchOption.getBoost());
					 
				 }
				 if (mySearchOption.getSearchLogic() == SearchLogic.should) {
					   boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
				 }
				 else{
					 boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
				 }
			 }
			return boolQueryBuilder;
		}
		catch(Exception e)
		{
			 this.logger.error(e.getMessage());
		}
	return null;
	}

	private QueryBuilder createRangeQueryBuilder(String field, Object[] values) {
		// TODO Auto-generated method stub
		if(values.length == 1 || values[1] == null || values[1].toString().trim().isEmpty()){
			this.logger.warn("[区间搜索]必须传递两个值，但是只传递了一个值，所以返回null");
			return null;
		}
		boolean timeType = false;
		if(MySearchOption.isDate(values[0]))
		{
			if(MySearchOption.isDate(values[1]))
			{
				timeType = true;
			}
		}
		String begin = "",end = "";
		if(timeType)
		{
			begin = MySearchOption.formatDate(values[0]);
			
			end = MySearchOption.formatDate(values[1]);
		}
		else
		{
			 begin = values[0].toString();
			 System.out.println(field + begin);
			 end = values[1].toString();
			 System.out.println( field + end);
		}
		return QueryBuilders.rangeQuery(field).from(begin).to(end);
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
			 queryBuilder = this.createQueryBuilder(searchContentMap, searchLogic);
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
	        QueryBuilder mustQueryBuilder = this.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
	        /*创建should搜索条件*/
	        QueryBuilder shouldQueryBuilder = this.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
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
