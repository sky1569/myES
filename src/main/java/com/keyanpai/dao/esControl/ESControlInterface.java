package com.keyanpai.dao.esControl;

import java.util.HashMap;
import java.util.List;

import org.elasticsearch.common.xcontent.XContentBuilder;



public interface ESControlInterface {
    boolean bulkInsert(List<XContentBuilder> dataList, 
			String index_name,String index_type);
    
	boolean bulkDelete(String[] indexName
			,HashMap<String, Object[]> contentMap);

	boolean bulkUpdate(String indexName
			,HashMap<String, Object[]> oldContentMap
			,HashMap<String, Object[]> newContentMap);	
	
	boolean deleteIndexByName(String indexName);
	
	boolean deleteIndexTemplate(String indexNameTemplate);
	
	boolean creatIndex(String indexName);
	
	boolean creatIndexTemplate(String templatePath,String indexName,String indexType);
}
