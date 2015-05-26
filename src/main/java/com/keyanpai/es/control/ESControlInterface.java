package com.keyanpai.es.control;

import java.util.HashMap;

import com.keyanpai.db.DBConfigure;

public interface ESControlInterface {

    boolean bulkInsert(DBConfigure dbConfigure);

	boolean bulkDelete(String[] indexName,
			HashMap<String, Object[]> contentMap);

	boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap);
	boolean creatIndexTemplate(String templatePath,String indexName,String indexType);
	boolean deleteIndex(String indexName);
	boolean deleteIndexTemplate(String indexNameTemplate);
	boolean creatIndex(String indexName);
}