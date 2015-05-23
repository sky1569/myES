package com.keyanpai.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import com.keyanpai.dbImp.DBServiceImp;
import com.keyanpai.esImp.ESControlImp;
import com.keyanpai.esImp.ESSearchImp;

import com.keyanpai.instance.DBConfigure;
import com.keyanpai.instance.ESClient;
import com.keyanpai.instance.MySearchOption.SearchLogic;


public class adminAccount extends account {
	private ESClient esClient = new ESClient();
	private ESControlImp esControlImp = ESControlImp.getInstance();
	private ESSearchImp esSearchImp = new ESSearchImp();
	private DBServiceImp dbServiceImp = DBServiceImp.getDBServiceImp();
	private Logger logger = Logger.getLogger(adminAccount.class);
	
	public adminAccount(String id
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

	public boolean bulkInsertFromMysql(List<String> clusterList,DBConfigure dbConfigure){
		try{
			this.getClientConn(clusterList);
			this.esControlImp.controlConfigure(this.esClient.getClient());
			this.dbServiceImp.DBSetter(dbConfigure);
			this.dbServiceImp.open();
			this.dbServiceImp.handlData(this.esControlImp);	
			this.dbServiceImp.close();
			this.getClientClosed();
			return true;
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;

	}	


   public boolean bulkUpdate(List<String> clusterList,String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub	
	     try{
			   this.getClientConn(clusterList);
			   this.esControlImp.controlConfigure(this.esClient.getClient());
			   this.esControlImp.setESSeachImp(this.esSearchImp);
			   this.esControlImp.bulkUpdate(indexName, oldContentMap, newContentMap);
			   this.getClientClosed();
			   return true;
		    }
			catch(Exception e)
			{
				this.logger.error(e.getMessage());
			}
			return false;
			}

	public boolean bulkDelete(List<String> clusterList,String[] indexName,
			HashMap<String, Object[]> contentMap) {
		// TODO Auto-generated method stub
		try{
			this.getClientConn(clusterList);
			this.esControlImp.controlConfigure(this.esClient.getClient());
			this.esControlImp.setESSeachImp(this.esSearchImp);
			this.esControlImp.bulkDelete(indexName, contentMap);
			this.getClientClosed();
			return false;
			}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;
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

	public long getCount(List<String> clusterList,String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		this.getClientConn(clusterList);		
		this.esSearchImp.searchConfigure(this.esClient.getClient());		
		long rs = this.esSearchImp.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic);
		this.getClientClosed();
		return rs;
	}

	public List<Map<String, Object>> getSuggest(String[] indexNames,
			String filedName, String value, int count) {
		// TODO Auto-generated method stub
		return null;
	}

//	public void up(){
//	System.out.println(this.getName()+":up!");
//}
//	public void down(){
//	System.out.println(this.getName()+":down!");
//}
//	public void comment(){
//	System.out.println(this.getName()+":comment!");
//}

//	public void update()
//　　{
//	System.out.println(this.getName()+":update!");		
//}
	

}
