package com.keyanpai.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;

import com.keyanpai.esImp.ESSearchImp;
import com.keyanpai.esInterface.ESSearch;
import com.keyanpai.instance.MySearchOption.SearchLogic;
import com.keyanpai.userInterface.UserControl;
import com.keypai.userImp.UserControlImp;

public class userAccount extends account implements ESSearch{

	private UserControlImp userControlImp;
	private ESSearchImp esSearchImp;
	public userAccount(){
		
	}
	public boolean searchConfigure(Client esClient) {
		// TODO Auto-generated method stub
		this.esSearchImp.searchConfigure(esClient);
		return true;
	}

	public List<Map<String, Object>> simpleSearch(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap,
			SearchLogic filterLogic, int from, int offset, String sortField,
			String sortType) {
		// TODO Auto-generated method stub
		return this.esSearchImp.simpleSearch(
				indexNames, searchContentMap, searchLogic
				, filterContentMap, filterLogic, from, offset, sortField, sortType);
	}

	public long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		return this.esSearchImp.getCount(
				indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic);
	}

	public List<Map<String, Object>> getSuggest(String[] indexNames,
			String filedName, String value, int count) {
		// TODO Auto-generated method stub
		return this.esSearchImp.getSuggest(indexNames, filedName, value, count);
	}

	

	
	

}
