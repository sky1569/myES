package com.keyanpai.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyanpai.es.MySearchOption.SearchLogic;
import com.keyanpai.es.client.ESClientImp;
import com.keyanpai.es.search.ESSearchImp;
import com.keyanpai.es.search.ESSearchInterface;

public class GuestAccount extends Account implements ESSearchInterface{
	public ESClientImp esClient = null;


	public void getClientClosed() {
		// TODO Auto-generated method stub
		this.esClient.clientDestroy();
	}

	public void getClientConn(List<String> clusterList) {
		// TODO Auto-generated method stub
		if(this.esClient != null)
			{
				this.getClientClosed();
				this.esClient = null;
			}
		this.esClient = new ESClientImp();
		this.esClient.ESClientConfigure(clusterList);
		this.esClient.clientConn();
	}
	
	public List<Map<String, Object>> search(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap,
			SearchLogic filterLogic, int from, int offset, String sortField,
			String sortType) {
		// TODO Auto-generated method stub
		ESSearchImp esSearchImp = new ESSearchImp(esClient.getClient());			
		List<Map<String, Object>> rs = esSearchImp.simpleSearch(
				indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
		return rs;		
	}
	
	public long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		ESSearchImp esSearchImp = new ESSearchImp(esClient.getClient());			
		long rs = esSearchImp.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic);
		return rs;
	}

	public List<Map<String, Object>> getSuggest(String[] indexNames,
			String filedName, String value, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> group(String indexName,
			HashMap<String, Object[]> mustSearchContentMap,
			HashMap<String, Object[]> shouldSearchContentMap,
			String[] groupFields) {
		// TODO Auto-generated method stub
		return null;
	}
}
