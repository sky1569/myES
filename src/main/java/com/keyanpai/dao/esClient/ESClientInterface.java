package com.keyanpai.dao.esClient;

import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;

public interface ESClientInterface {
	
	public  boolean clientConn() ;
	public  boolean clientDestroy();
	public  Client getClient();
/**
 * 
 * @param queryBuilder
 * @param indexNames
 * @param indexTypes
 * @param from
 * @param offset
 * @param sortField
 * @param sortType
 * @return
 */
	public  SearchResponse query(QueryBuilder queryBuilder
			,String[] indexNames,String[] indexTypes
			,int from, int offset
			,String sortField
			,String sortType);
	/**
	 * 
	 * @param dataList
	 * @param index_name
	 * @param index_type
	 * @return
	 */
	public  boolean insert(List<XContentBuilder> dataList,
			String index_name, String index_type);
	/**
	 * 
	 * @param indexName
	 * @param queryBuilder
	 * @return
	 */
	public  boolean deleteByQuery(String[] indexName,String[] indexTypes,QueryBuilder queryBuilder);
	
	
	//public  boolean updateByQuery();
	
	boolean deleteIndexByName(String[] indexNames);
	
	boolean deleteIndexTemplate(String indexNameTemplate);
	
	boolean creatIndex(String indexName);
	
	//boolean creatIndexTemplate(String templatePath,String indexName,String indexType);
}
