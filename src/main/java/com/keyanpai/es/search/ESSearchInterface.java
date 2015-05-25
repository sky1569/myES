package com.keyanpai.es.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.elasticsearch.common.Nullable;

import com.keyanpai.es.MySearchOption.SearchLogic;

public interface ESSearchInterface {
	/**
	 * 
	 * @param clusterList
	 * @param indexName
	 * @param indexType
	 * @return
	 */
//	 boolean searchConfigure(
//			 Client esClient);
	
	/**
	 * 
	 * @param indexNames
	 * @param searchContentMap
	 * @param searchLogic
	 * @param filterContentMap
	 * @param filterLogic
	 * @param from
	 * @param offset
	 * @param sortField
	 * @param sortType
	 * @return
	 */
	 List<Map<String, Object>> search(String[] indexNames
            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic
            , int from, int offset, @Nullable String sortField, @Nullable String sortType);
	
	/**
	 * 
	 * @param indexNames
	 * @param searchContentMap
	 * @param searchLogic
	 * @param filterContentMap
	 * @param filterLogic
	 * @return
	 */
	 long getCount(String[] indexNames,HashMap<String,Object[]> searchContentMap
			,SearchLogic searchLogic,@Nullable HashMap<String,Object[]> filterContentMap
			,@Nullable SearchLogic filterLogic);
	/**
	 * 
	 * @param indexNames
	 * @param filedName
	 * @param value
	 * @param count
	 * @return
	 */
	List<Map<String ,Object> > getSuggest(
			String[] indexNames,String filedName,String value,int count); 
	/**
	 * 
	 * @param indexName
	 * @param mustSearchContentMap
	 * @param shouldSearchContentMap
	 * @param groupFields
	 * @return
	 */
    Map<String, String> group(String indexName
	            , HashMap<String, Object[]> mustSearchContentMap
	            , HashMap<String, Object[]> shouldSearchContentMap
	            , String[] groupFields);
}
