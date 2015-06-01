package com.keyanpai.service.esSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;








import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.search.SearchHit;
import com.keyanpai.common.ESCreatQueryBuilder;
import com.keyanpai.common.MySearchOption;
import com.keyanpai.common.MySearchOption.DataFilter;
import com.keyanpai.common.MySearchOption.SearchLogic;
import com.keyanpai.dao.esClient.CommonPool2;
import com.keyanpai.dao.esClient.ESClientImp;


public class ESSearchImp implements ESSearchInterface {
	
	
	private CommonPool2 cp2 = CommonPool2.getCommonPool2() ;
	private ESCreatQueryBuilder esCreatQueryBuilder = null;
	private Logger logger = Logger.getLogger("Service.ESSearchImp");	

	public ESCreatQueryBuilder getEsCreatQueryBuilder() {
		return esCreatQueryBuilder;
	}
	public void setEsCreatQueryBuilder(ESCreatQueryBuilder esCreatQueryBuilder) {
		this.esCreatQueryBuilder = esCreatQueryBuilder;
	}
	public void setCommonPool2(CommonPool2 cp2) {		
			this.cp2 = cp2;
	}

	public List<Map<String, Object>> simpleSearch(String[] indexNames,String[] indexTypes
			,HashMap<String, Object[]> searchContentMap
			,SearchLogic searchLogic
			,HashMap<String, Object[]> filterContentMap
			,SearchLogic filterLogic
			,int from, int offset
			,String sortField
			,String sortType) {
		// TODO Auto-generated method stub		
		if(offset <= 0){
			return null;
		}
			QueryBuilder queryBuilder = null;
			queryBuilder = this.esCreatQueryBuilder.createQueryBuilder(searchContentMap,searchLogic);
			queryBuilder = this.createFilterBuilder(filterLogic,queryBuilder,searchContentMap
					,filterContentMap);
			ESClientImp esClient = null;
			try{
				esClient = cp2.borrowObject();
				return this.getSearchResult(esClient.query(queryBuilder, indexNames, indexTypes, from, offset, sortField, sortType));
			}
			catch(Exception e){
				this.logger.error(e.getMessage());
			}
			finally{
				cp2.returnObject(esClient);
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
					resultMap.put("uuid", searchHit.getId()+searchHit.getType());
				while(iterator.hasNext()){
					Entry<String, Object> entry = iterator.next();
					resultMap.put(entry.getKey(), entry.getValue());
				}
//				 Map<String, HighlightField> highlightMap = searchHit.highlightFields();
//				 Iterator<Entry<String, HighlightField>> highlightIterator = highlightMap
//						 .entrySet().iterator();
//				 
//				while (highlightIterator.hasNext()) {
//					   Entry<String, HighlightField> entry = highlightIterator.next();
//					   Object[] contents = entry.getValue().fragments();
//					   if (contents.length == 1) {
//						   resultMap.put(entry.getKey(), contents[0].toString());
//						   System.out.println("!!!"+contents[0].toString());						   
//					   }
//					   else {
//						   System.out.println("搜索结果中的高亮结果出现多数据contents.length = " + contents.length);
//					   }
//				}
				 this.logger.debug(resultMap.toString());		
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

	public long getCount(String[] indexNames,String[] indexTypes
			,HashMap<String, Object[]> searchContentMap
			,SearchLogic searchLogic
			,HashMap<String, Object[]> filterContentMap
			,SearchLogic filterLogic
			) {
		// TODO Auto-generated method stub
		QueryBuilder queryBuilder = null;		
		queryBuilder = this.esCreatQueryBuilder.createQueryBuilder(searchContentMap,searchLogic);
		queryBuilder = this.createFilterBuilder(filterLogic,queryBuilder,searchContentMap
						,filterContentMap);
				
		ESClientImp esClient = null;
		try{
			esClient = cp2.borrowObject();
			return esClient.query(
							 queryBuilder, indexNames, indexTypes, 0, 1, null, null)
							 .getHits().totalHits();
		}
		catch(Exception e){
				this.logger.error(e.getMessage());
		}
		finally{
				cp2.returnObject(esClient);
		}			
		
	
	
		return 0;
		
	}

//	 private Map<String, String> _group(String indexName, QueryBuilder queryBuilder, String[] groupFields) {
//	        try {
//	            TermsFacetBuilder termsFacetBuilder = FacetBuilders.termsFacet("group").fields(groupFields).size(9999);
//	            SearchRequestBuilder searchRequestBuilder = this.ESClient.prepareSearch(indexName).setSearchType(SearchType.DEFAULT)
//	                    .addFacet(termsFacetBuilder).setQuery(queryBuilder).setFrom(0).setSize(1).setExplain(true);
//	            this.logger.debug(searchRequestBuilder.toString());
//	            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
//	            TermsFacet termsFacet = searchResponse.getFacets().facet("group");
//	            HashMap<String, String> result = new HashMap<String, String>();
//	            for (org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : termsFacet.getEntries()) {
//	                result.put(entry.getTerm()+"", entry.getCount()+"");
//	            }
//	            return result;
//	        }
//	        catch (Exception e) {
//	            this.logger.error(e.getMessage());
//	        }
//	        return null;
//	    }
//
//	    public Map<String, String> group(String indexName
//	            , HashMap<String, Object[]> mustSearchContentMap
//	            , HashMap<String, Object[]> shouldSearchContentMap
//	            , String[] groupFields) {
//	        /*创建must搜索条件*/
//	        QueryBuilder mustQueryBuilder = this.esCreatQueryBuilder.createQueryBuilder(mustSearchContentMap, SearchLogic.must);
//	        /*创建should搜索条件*/
//	        QueryBuilder shouldQueryBuilder = this.esCreatQueryBuilder.createQueryBuilder(shouldSearchContentMap, SearchLogic.should);
//	        if (mustQueryBuilder == null && shouldQueryBuilder == null) {
//	            return null;
//	        }
//	        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//	        if (mustQueryBuilder != null) {
//	            boolQueryBuilder = boolQueryBuilder.must(mustQueryBuilder);
//	        }
//	        if (shouldQueryBuilder != null) {
//	            boolQueryBuilder = boolQueryBuilder.must(shouldQueryBuilder);
//	        }
//	        try {
//	            return this._group(indexName, boolQueryBuilder, groupFields);
//	        }
//	        catch (Exception e) {
//	            this.logger.error(e.getMessage());
//	        }
//	        return null;
//	    }

	

}
