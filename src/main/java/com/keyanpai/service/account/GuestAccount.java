package com.keyanpai.service.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.keyanpai.common.MySearchOption.SearchLogic;
import com.keyanpai.service.esSearch.ESSearchImp;



public class GuestAccount extends Account implements AccountSearchInterface{

	public ESSearchImp esSearchImp ;

	public ESSearchImp getEsSearchImp() {
		return esSearchImp;
	}

	public void setEsSearchImp(ESSearchImp esSearchImp) {
		this.esSearchImp = esSearchImp;
	}

	public GuestAccount(){
	}
	
	public List<Map<String, Object>> search(String[] indexNames
			,String[] indexTypes
			,HashMap<String, Object[]> searchContentMap
			,SearchLogic searchLogic
			,HashMap<String, Object[]> filterContentMap
			,SearchLogic filterLogic
			,int from, int offset
			,String sortField
			,String sortType) {
		// TODO Auto-generated method stub	

		List<Map<String, Object>> rs = esSearchImp.simpleSearch(
				indexNames, indexTypes, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
		return rs;		
	}
	
	
	public long getCount(String[] indexNames
			,String[] indexTypes
			,HashMap<String, Object[]> searchContentMap
			,SearchLogic searchLogic
			,HashMap<String, Object[]> filterContentMap
			,SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		long rs = esSearchImp.getCount(indexNames, indexTypes, searchContentMap, searchLogic, filterContentMap, filterLogic);
		return rs;
	}

}
