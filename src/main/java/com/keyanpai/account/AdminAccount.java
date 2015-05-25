package com.keyanpai.account;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.keyanpai.db.DBConfigure;
import com.keyanpai.db.DBServiceImp;
import com.keyanpai.es.control.ESControlImp;
import com.keyanpai.es.control.ESControlInterface;


public class AdminAccount extends UserAccount implements ESControlInterface {
//	private ESClientImp esClient = null;
	private ESControlImp esControlImp = ESControlImp.getInstance();
	private Logger logger = Logger.getLogger(AdminAccount.class);
	
	public AdminAccount(String id
					   ,String ip
					   ,String name
					   ,String password
					   )
	{	super(id, ip, name, password);
		PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
	}	
	


	public boolean bulkInsert(DBConfigure dbConfigure) {
		// TODO Auto-generated method stub
		DBServiceImp dbServiceImp = DBServiceImp.getDBServiceImp();
		try{			
					
			this.esControlImp.controlConfigure(esClient.getClient());
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
		
			   this.esControlImp.controlConfigure(esClient.getClient());		
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
			this.esControlImp.controlConfigure(esClient.getClient());					
			return this.esControlImp.bulkDelete(indexName, contentMap);
			}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;
	}






	public boolean creatIndex(String indexName) {
		// TODO Auto-generated method stub
		this.esControlImp.controlConfigure(esClient.getClient());
		return this.esControlImp.creatIndex( indexName);
		 
	}
	public boolean creatIndexTemplate(String templatePath, String indexTemplateName,
			String indexType) {
		// TODO Auto-generated method stub
		this.esControlImp.controlConfigure(esClient.getClient());
		return this.esControlImp.creatIndexTemplate(templatePath, indexTemplateName, indexType);
		 
	}

	public boolean deleteIndex(String indexName) {
		// TODO Auto-generated method stub
		this.esControlImp.controlConfigure(esClient.getClient());
		return this.esControlImp.deleteIndexName(indexName);
	}

	public boolean deleteIndexTemplate(String indexNameTemplate) {
		// TODO Auto-generated method stub
		this.esControlImp.controlConfigure(esClient.getClient());
		return this.esControlImp.deleteIndexTemplate(indexNameTemplate);
	}

//	public boolean update(
//			List<String> clusterList,String indexName
//			,String indexType,String _id,HashMap<String ,Object[]> newContentMap)
//	{
//		System.out.println(this.getName()+":update!");		
//	
//		this.esControlImp.controlConfigure(esClient.getClient());
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
