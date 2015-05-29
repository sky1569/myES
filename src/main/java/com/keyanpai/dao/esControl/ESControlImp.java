package com.keyanpai.dao.esControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;


import com.keyanpai.common.ESCreatQueryBuilder;
import com.keyanpai.common.MySearchOption;
import com.keyanpai.common.MySearchOption.SearchLogic;
import com.keyanpai.dao.esSearch.ESSearchImp;

public class ESControlImp extends ESControl implements ESControlInterface{
	
	private Client ESClient = null;
	private ESCreatQueryBuilder esCreatQueryBuilder = new  ESCreatQueryBuilder();	
	private Logger logger = Logger.getLogger("DAO.ESControlImp");
	public ESControlImp(Client esClient){this.ESClient = esClient;}	
	
	public boolean creatIndexTemplate(String template
			,String indexTemplateName
			,String indexType)
	{
				try{		
					 PutIndexTemplateResponse putIndexTemplateResponse =  
							 this.ESClient.admin().indices()
							 .preparePutTemplate(indexTemplateName)
							 .setSource(template).execute().actionGet();
					if(putIndexTemplateResponse.isAcknowledged()){
						return true;
					}
					else{
						this.logger.error("创建索引失败");
						return false;
					}					
				}
				catch(Exception e){			
					this.logger.error(e.getMessage());
					return false;
				}
	}
	
	public boolean bulkInsert(List<XContentBuilder> dataList, 
			String index_name,String index_type) {
		// TODO Auto-generated method stub
		return this._bulkInsertData(dataList,index_name,index_type) ;		
	}

	
	
	private boolean _bulkInsertData(List<XContentBuilder> dataList,
			String index_name, String index_type) {
		// TODO Auto-generated method stub			
		try{			
    		BulkRequestBuilder bulk = this.ESClient.prepareBulk();    	
    		for(XContentBuilder xc : dataList){
    			IndexRequest request = new IndexRequest(index_name, index_type);
    			request.source(xc);
    			bulk.add(request);
    		}
    		BulkResponse bulkResponse = bulk.execute().actionGet();    	
    		if (!bulkResponse.hasFailures()) {
    			 return true;
    		}
    		 else {
    			 this.logger.error(bulkResponse.buildFailureMessage());
    		 }
    	}
    	catch(Exception e) 	{
    		 this.logger.error(e.getMessage());
    	}    	
		return false;    	
	}
	
	public boolean bulkDelete(String[] indexName,
			HashMap<String, Object[]> contentMap) {
		// TODO Auto-generated method stub
		try{
			QueryBuilder queryBuilder = null;
			queryBuilder = this.esCreatQueryBuilder.createQueryBuilder(contentMap, SearchLogic.must);
			this.logger.warn("[" + indexName + "]" + queryBuilder.toString());
			this.ESClient.prepareDeleteByQuery(indexName).setQuery(queryBuilder).execute().actionGet();		
			return true;
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
		return false;
		
	}
	
	private  List<Map<String, Object>> simpleSearch(String[] indexNames,
	HashMap<String, Object[]> searchContentMap,
	HashMap<String, Object[]> filterContentMap, int from, int offset,
	 String sortField, String sortType) {
	// TODO Auto-generated method stub
		SearchLogic searchLogic = indexNames.length > 1 ? SearchLogic.should : SearchLogic.must;
		ESSearchImp esSearchImp = new ESSearchImp(this.ESClient);
		return esSearchImp.simpleSearch(
		indexNames, searchContentMap, searchLogic, filterContentMap
		, searchLogic, from, offset, sortField, sortType);
}
	public boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub
		try{
			List<Map<String ,Object>> searchResult = this.simpleSearch(
					new String[]{indexName}, oldContentMap, null, 0,1,null, null);
			if (searchResult == null || searchResult.size() == 0) {
				  //this.logger.warn("未找到需要更新的数据");
				  return false;
			}
			if (!this.bulkDeleteData(indexName, oldContentMap)) {
				//this.logger.warn("删除数据失败");
				 return false;
			}
			HashMap<String, Object[]> insertContentMap = new HashMap<String, Object[]>();
			for (Map<String, Object> contentMap : searchResult) {
				Iterator<Entry<String, Object>> oldContentIterator = contentMap.entrySet().iterator();
				 while (oldContentIterator.hasNext()) {
					 Entry<String, Object> entry = oldContentIterator.next();
					 insertContentMap.put(entry.getKey(), new Object[] { entry.getValue() });
				 }
			}
			Iterator<Entry<String, Object[]>> newContentIterator = newContentMap.entrySet().iterator();
			while (newContentIterator.hasNext()) {
				 Entry<String, Object[]> entry = newContentIterator.next();
				 insertContentMap.put(entry.getKey(), entry.getValue());
			}
			return this.bulkInsertData(indexName, insertContentMap);
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;
	}
	private boolean bulkDeleteData(String indexName,
			HashMap<String, Object[]> contentMap) {
		// TODO Auto-generated method stub
		try{
			QueryBuilder queryBuilder = null;
			queryBuilder = this.esCreatQueryBuilder.createQueryBuilder(contentMap, SearchLogic.must);
			this.logger.warn("[" + indexName + "]" + queryBuilder.toString());
			this.ESClient.prepareDeleteByQuery(indexName).setQuery(queryBuilder).execute().actionGet();
		
			return true;
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;
	}

	private boolean bulkInsertData(String indexName,
			HashMap<String, Object[]> insertContentMap) {
		// TODO Auto-generated method stub
		 XContentBuilder xContentBuilder = null;
		 try {
			 xContentBuilder = XContentFactory.jsonBuilder().startObject();
		 }
		 catch (IOException e) {
			 this.logger.error(e.getMessage());
			   return false;
		 }
		  Iterator<Entry<String, Object[]>> iterator = insertContentMap.entrySet().iterator();
		  while (iterator.hasNext()) {
			  Entry<String, Object[]> entry = iterator.next();
			  String field = entry.getKey();
			  Object[] values = entry.getValue();
			  String formatValue = this.formatInsertData(values);
			  try {
				  xContentBuilder = xContentBuilder.field(field, formatValue);
			  }
			  catch (IOException e) {
				  this.logger.error(e.getMessage());
				   return false;
			  }
		  }
		  try {
			  xContentBuilder = xContentBuilder.endObject();
		  }
		  catch (IOException e) {
			  this.logger.error(e.getMessage());
			  return false;
		  }
		  return this._bulkInsertData(indexName, xContentBuilder);
	}
	
	private boolean _bulkInsertData(String indexName,
			XContentBuilder xContentBuilder) {
		// TODO Auto-generated method stub
		 try {
	            BulkRequestBuilder bulkRequest = this.ESClient.prepareBulk();
	            bulkRequest.add(this.ESClient.prepareIndex(indexName, indexName).setSource(xContentBuilder));
	            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
	            if (!bulkResponse.hasFailures()) {
	                return true;
	            }
	            else {
	                this.logger.error(bulkResponse.buildFailureMessage());
	            }
	        }
	        catch (Exception e) {
	            this.logger.error(e.getMessage());
	        }
	        return false;
	}
	private String formatInsertData(Object[] values) {
        if (!this.checkValue(values)) {
            return "";
        }
        if (MySearchOption.isDate(values[0])) {
            this.logger.warn("[" + values[0].toString() + "] formatDate");
            return MySearchOption.formatDate(values[0]);
        }
        String formatValue = values[0].toString();
        for (int index = 1; index < values.length; ++index) {
            formatValue += "," + values[index].toString();
        }
        return formatValue.trim();
    }
	private boolean checkValue(Object[] values) {
		// TODO Auto-generated method stub
			if(values == null)
				return false;
			else if(values.length == 0)
				return false;
			else if(values[0] == null)
				return false;
			else if(values[0].toString().trim().isEmpty())
				return false;
		return true;
		}
	
	


//根据内容_id进行局部字段更新
//	public boolean update(String indexName
//			,String indexType
//			,String _id,HashMap<String,Object[]> newContentMap
//			) {
//		UpdateRequest updateRequest = new UpdateRequest(indexName,indexType,_id);
//		try {
//			XContentBuilder xc = XContentFactory.jsonBuilder().startObject();
//			for (Entry<String,Object[]> colunm : newContentMap.entrySet()) {
//				Object[] ob = colunm.getValue();
//				String field = colunm.getKey();
//				if(1 == ob.length){
//					xc.field(field, ob[0]);
//				}
//				else if(1 < ob.length){
//				   xc.array(field, ob);
//				}
//			}
//			xc.endObject();
//			updateRequest.doc(xc);	
//				try {
//					this.ESClient.update(updateRequest).get();
//				} catch (InterruptedException | ExecutionException e) {
//					// TODO Auto-generated catch block
//					this.logger.error(e.getMessage());
//				}		
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			this.logger.error(e.getMessage());
//		}
//		
//		return true;
//	}
	
  public boolean deleteIndexByName(String indexName)
  {		 try{
			  DeleteIndexResponse delete = this.ESClient
					  .admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
			  if (!delete.isAcknowledged()) {
				   this.logger.error(indexName+ " wasn't deleted");
				   return false;
				}
			  else return true;		
  		}
  		catch(Exception e)
  		{
  			this.logger.error(e.getMessage());
  			return false;
  		}
  }
  
  public boolean deleteIndexTemplate(String deleteIndexTemplateName)
  {
	  try{
	  DeleteIndexTemplateResponse delete = this.ESClient
			  .admin().indices().deleteTemplate(
					  new DeleteIndexTemplateRequest(deleteIndexTemplateName)).actionGet();
	  if (!delete.isAcknowledged()) {
		   this.logger.error(deleteIndexTemplateName + " wasn't deleted");
		   return false;
		}
	  else return true;	
	  }
	  catch(Exception e) {
		 this.logger.error(e.getMessage());
	  }
	  return false;
  }
  
	public boolean creatIndex(String indexName) {
		// TODO Auto-generated method stub
		CreateIndexResponse create = this.ESClient.admin().indices().create(
				new CreateIndexRequest(indexName)).actionGet();
		if (!create.isAcknowledged()) {
			  this.logger.error(indexName  +" was created");
			   return false;
			}
		  else return true;	
	}
}
