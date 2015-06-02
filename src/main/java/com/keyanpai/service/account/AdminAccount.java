package com.keyanpai.service.account;

import java.util.HashMap;

import org.apache.log4j.Logger;


import com.keyanpai.common.DBConfigure;
import com.keyanpai.dao.db.DBServiceImp;
import com.keyanpai.service.esControl.ESControlImp;



public class AdminAccount extends UserAccount implements AccountControlInterface {

	private ESControlImp esControlImp = null;
	private Logger logger = Logger.getLogger("Service.Admin");	
	public AdminAccount(){
//		BeanFactory beanFactory = new ClassPathXmlApplicationContext("beans.xml");
//		esControlImp  =  beanFactory.getBean("esControlImp",ESControlImp.class );
	}
	public AdminAccount(String id
					   ,String ip
					   ,String name
					   ,String password				
					   )
	{		
		super(id,ip,name,password);
	}	
	public boolean bulkInsert(DBConfigure dbConfigure) {
		// TODO Auto-generated method stub
		DBServiceImp dbServiceImp = DBServiceImp.getDBServiceImp();
		try{			
			dbServiceImp.DBSetter(dbConfigure);
			dbServiceImp.open();							
			return dbServiceImp.handlData(this.esControlImp);
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
		finally{
			dbServiceImp.close();
		}
		return false;
	}
	public boolean bulkDelete(String[] indexNames,String[] indexTypes,
			HashMap<String, Object[]> contentMap) {
		// TODO Auto-generated method stub					
		return this.esControlImp.bulkDelete(indexNames, indexTypes, contentMap);	
	}


   public boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub	
//	   return this.esControlImp.bulkUpdate(indexName, oldContentMap, newContentMap);
	   this.logger.error("还没这功能");
	   return false;
   }

	public boolean deleteIndexByName(String[] indexNames) {
		// TODO Auto-generated method stub		
		return this.esControlImp.deleteIndexByName(indexNames);
	}

	public boolean creatIndex(String indexName) {
		// TODO Auto-generated method stub		
		return this.esControlImp.creatIndex( indexName);		 
	}

	public boolean creatIndexTemplate(String templatePath, String indexTemplateName,
			String indexType) {
		// TODO Auto-generated method stub		
		//	return this.esControlImp.creatIndexTemplate(templatePath, indexTemplateName, indexType);		 
		this.logger.error("还没这功能");
		   return false;
	}



	public boolean deleteIndexTemplate(String indexNameTemplate) {
		// TODO Auto-generated method stub
		return this.esControlImp.deleteIndexTemplate(indexNameTemplate);
	}

//	public boolean update(
//			List<String> clusterList,String indexName
//			,String indexType,String _id,HashMap<String ,Object[]> newContentMap)
//	{
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
