package com.keyanpai.esInterface;

import java.util.HashMap;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

public interface ESControl {

	/**
	 * 
	 * @param esClient
	 * @return
	 */
//	boolean controlConfigure(
//			Client esClient);
	/**
	 * 
	 * @param dataList
	 * @param index_id
	 * @param index_name
	 * @param index_type
	 * @return
	 */
    boolean bulkInsert(
			List<XContentBuilder> dataList,String index_id,String index_name
			,String index_type);
	/**
	 * 
	 * @param indexName
	 * @param contentMap
	 * @return
	 */
	boolean bulkDelete(String[] indexName, HashMap<String, Object[]> contentMap);
	boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap);
	/**
	 * 
	 * @param indexName
	 * @param oldContentMap
	 * @param newContentMap
	 * @return
	 */
	boolean update(
			String indexName ,HashMap<String ,Object[]>oldContentMap,HashMap<String,Object[]>newContentMap);
	//public boolean delete();
	
}
