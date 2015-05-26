package com.keyanpai.es.control;

import java.util.HashMap;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;


 abstract class ESControl {

	 public  abstract boolean controlConfigure(Client esClient);
	 
	 public  abstract boolean bulkInsert(List<XContentBuilder> dataList,
				String index_name, String index_type);
	 public  abstract boolean bulkDelete(String[] indexName,
				HashMap<String, Object[]> contentMap);
	 public  abstract boolean bulkUpdate(String indexName,
				HashMap<String, Object[]> oldContentMap,
				HashMap<String, Object[]> newContentMap);
	 
}
