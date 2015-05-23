package com.keyanpai.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.keyanpai.dbImp.DBServiceImp;
import com.keyanpai.esImp.ESControlImp;
import com.keyanpai.esImp.ESSearchImp;
import com.keyanpai.instance.ESClient;
import com.keyanpai.instance.MySearchOption.SearchLogic;

public class guestAccount extends account{
	private ESClient esClient = new ESClient();	
	private ESSearchImp esSearchImp = new ESSearchImp();	
	private Logger logger = Logger.getLogger(adminAccount.class);
	
	public guestAccount(String id
					   ,String ip
					   ,String name
					   ,String password
					   )
	{
		this.setId(id);
		this.setIp(ip);
		this.setName(name);
		this.setPassword(password);
		this.setAccountType();
		PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
	}	
	
	private void getClientClosed() {
		// TODO Auto-generated method stub
		this.esClient.destroy();		
	}

	private void getClientConn(List<String> clusterList) {
		// TODO Auto-generated method stub
		this.esClient.ESClientConfigure(clusterList);
		this.esClient.clientConn();
	}
	public List<Map<String, Object>> search(List<String> clusterList,String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap,
			SearchLogic filterLogic, int from, int offset, String sortField,
			String sortType) {
		// TODO Auto-generated method stub
		this.getClientConn(clusterList);		
		this.esSearchImp.searchConfigure(this.esClient.getClient());		
		List<Map<String, Object>> rs = this.esSearchImp.simpleSearch(
				indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
		this.getClientClosed();
		return rs;
	}
}
