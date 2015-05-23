package com.keyanpai.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.keyanpai.esInterface.ESControl;
import com.keyanpai.esInterface.ESSearch;
import com.keyanpai.instance.MySearchOption.SearchLogic;
import com.keyanpai.userInterface.UserControl;

public class adminAccount extends account implements ESControl,UserControl,ESSearch{

	public boolean controlConfigure(Client esClient) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean bulkInsert(List<XContentBuilder> dataList, String index_id,
			String index_name, String index_type) {
		// TODO Auto-generated method stub
		return false;
	}	

	public boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean searchConfigure(Client esClient) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<Map<String, Object>> simpleSearch(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap,
			SearchLogic filterLogic, int from, int offset, String sortField,
			String sortType) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Map<String, Object>> getSuggest(String[] indexNames,
			String filedName, String value, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean bulkDelete(String[] indexName,
			HashMap<String, Object[]> contentMap) {
		// TODO Auto-generated method stub
		return false;
	}

}
