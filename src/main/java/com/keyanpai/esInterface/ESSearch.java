package com.keyanpai.esInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;

import com.keyanpai.instance.MySearchOption.SearchLogic;

public interface ESSearch {
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
	 List<Map<String, Object>> simpleSearch(String[] indexNames
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
}
