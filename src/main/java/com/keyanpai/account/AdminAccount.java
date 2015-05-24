package com.keyanpai.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import com.keyanpai.db.DBConfigure;
import com.keyanpai.db.DBServiceImp;
import com.keyanpai.es.MySearchOption.SearchLogic;

import com.keyanpai.es.client.ESClientImp;
import com.keyanpai.es.control.ESControlImp;
import com.keyanpai.es.control.ESControlInterface;
import com.keyanpai.es.search.ESSearchImp;
import com.keyanpai.es.search.ESSearchInterface;


public class AdminAccount extends Account implements ESControlInterface,ESSearchInterface {
	private ESClientImp esClient = null;
	private ESControlImp esControlImp = ESControlImp.getInstance();
	//private DBServiceImp dbServiceImp = DBServiceImp.getDBServiceImp();
	private Logger logger = Logger.getLogger(AdminAccount.class);
	
	ExecutorService executor = Executors.newFixedThreadPool(3);
	
	
	public AdminAccount(String id
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

	public boolean bulkInsert(DBConfigure dbConfigure) {
		// TODO Auto-generated method stub
		DBServiceImp dbServiceImp = DBServiceImp.getDBServiceImp();
		try{			
					
			this.esControlImp.controlConfigure(this.esClient.getClient());
			dbServiceImp.DBSetter(dbConfigure);
			dbServiceImp.open();							
			return dbServiceImp.handlData(this.esControlImp);
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		finally{
			dbServiceImp.close();
		}
		return false;
	}


   public boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub	
	     try{
		
			   this.esControlImp.controlConfigure(this.esClient.getClient());		
			   return this.esControlImp.bulkUpdate(indexName, oldContentMap, newContentMap);
		    }
			catch(Exception e)
			{
				this.logger.error(e.getMessage());
			}
			return false;
			}

	public boolean bulkDelete(String[] indexName,
			HashMap<String, Object[]> contentMap) {
		// TODO Auto-generated method stub
		try{			
			this.esControlImp.controlConfigure(this.esClient.getClient());					
			return this.esControlImp.bulkDelete(indexName, contentMap);
			}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;
	}

	public long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap, SearchLogic filterLogic) {
		// TODO Auto-generated method stub
		ESSearchImp esSearchImp = new ESSearchImp(this.esClient.getClient());			
		long rs = esSearchImp.getCount(indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic);
		return rs;
	}

	public List<Map<String, Object>> search(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			HashMap<String, Object[]> filterContentMap,
			SearchLogic filterLogic, int from, int offset, String sortField,
			String sortType) {
		// TODO Auto-generated method stub
		ESSearchImp esSearchImp = new ESSearchImp(this.esClient.getClient());			
		List<Map<String, Object>> rs = esSearchImp.simpleSearch(
				indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
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


//	public void up(){
//	System.out.println(this.getName()+":up!");
//}
//	public void down(){
//	System.out.println(this.getName()+":down!");
//}
//	public void comment(){
//	System.out.println(this.getName()+":comment!");
//}

//	public boolean update(
//			List<String> clusterList,String indexName
//			,String indexType,String _id,HashMap<String ,Object[]> newContentMap)
//	{
//		System.out.println(this.getName()+":update!");		
//	
//		this.esControlImp.controlConfigure(this.esClient.getClient());
//		try {
//			this.esControlImp.update(indexName, indexType, _id, newContentMap);
//			
//			return true;
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally
//		{
//			this.getClientClosed();
//		}
//		return false;
//	
//	}





}
