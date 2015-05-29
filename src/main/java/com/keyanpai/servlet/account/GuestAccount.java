package com.keyanpai.servlet.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyanpai.common.MySearchOption.SearchLogic;
import com.keyanpai.common.esClient.ESClient;
import com.keyanpai.dao.esSearch.ESSearchImp;


public class GuestAccount extends Account implements AccountSearchInterface{
	public ESClient esClient = null;	
	public ESSearchImp esSearchImp = new ESSearchImp(esClient.getClient());	
	public GuestAccount(ESClient es)
	{
		this.esClient = es ;
	}
	public List<Map<String, Object>> search(String[] indexNames
			,HashMap<String, Object[]> searchContentMap
			,SearchLogic searchLogic
			,HashMap<String, Object[]> filterContentMap
			,SearchLogic filterLogic
			,int from, int offset
			,String sortField
			,String sortType) {
		// TODO Auto-generated method stub			
		List<Map<String, Object>> rs = esSearchImp.simpleSearch(
				indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
		return rs;		
	}
	
	public long getCount(String[] indexNames
			,HashMap<String, Object[]> searchContentMap
			,SearchLogic searchLogic
			,HashMap<String, Object[]> filterContentMap
			,SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		ESSearchImp esSearchImp = new ESSearchImp(esClient.getClient());			
		long rs = esSearchImp.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic);
		return rs;
	}

}
