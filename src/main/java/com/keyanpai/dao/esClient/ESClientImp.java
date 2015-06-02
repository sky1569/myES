package com.keyanpai.dao.esClient;


import java.util.ArrayList;
import java.util.List;





import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;





public class ESClientImp implements ESClientInterface{
	private Logger logger = Logger.getLogger("DAO.ESClientImp");
	private List<String> clusterList = null;
	private Client ESClient = null;
	
	
	
	/*连接操作*/
	public boolean  clientConn()  {
		  this.logger.info("读取配置文件"); 
		  this._readConfigure();
		  this.logger.info("连接搜索服务器"); 
         return this._open();
     
   }	
	
	private void _readConfigure(){		
		if(null == this.clusterList){
				this.clusterList = new ArrayList<String>();
				this.clusterList.add("mitc-elastic:10.107.6.82:9300");
			}
		else{
			//循环读取配置文件
		}	
	
	}
	private boolean _open() {
		// TODO Auto-generated method stub
			 /*100s没有链接上搜索服务器，则超时*/
		try{	
			 Settings settings = ImmutableSettings.settingsBuilder()
					// .put(this.ESClientConfigureMap)
						.put("client.transport.ping_timeout", 100)
						.put("cluster.name", "mitc-elastic").build();
			 /*创建搜素客户端*/	
			 this.ESClient = new TransportClient(settings);
			 //debug 判断是否为空
			 if(this.clusterList.isEmpty())
				 this.logger.debug("clusterList is empty");
			 else
				 this.logger.debug(clusterList.toString());
			 for(String item : this.clusterList)
			 {
				 String address = item.split(":")[1];
				 int port = Integer.parseInt(item.split(":")[2]);				
				 /*通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配*/
				 this.ESClient = ((TransportClient) this.ESClient)
						 .addTransportAddress(new InetSocketTransportAddress(address, port));
				if(((TransportClient)this.ESClient).connectedNodes().isEmpty())
				{
					this.logger.error("连接失败，检查节点是否开启elasticsearch");
					return false;
				}
				 this.logger.info("连接成功");
				
			 }			
			 return true;
		}
		catch(Exception e){
			this.logger.error("连接错误");
			this.logger.error(e.getMessage());
		}
			return false;
	}			 
	
	public boolean clientDestroy() {
        this.logger.info("关闭搜索客户端");
        return this._close();
    }

	private boolean _close(){
	       if (this.ESClient == null) {return true;}
	     try{
	    	   this.ESClient.threadPool().shutdown();
		       this.ESClient.close();
		       this.ESClient = null;
		       return true;
	     	}
	     catch(Exception e)
	     {
	    	 this.logger.error(e.getMessage());
	     }
	     return false;
	}	
	
	
	public Client getClient()
	{
	
		return this.ESClient;
	}
	
	public SearchResponse  query(QueryBuilder queryBuilder
			,String[] indexNames,String[] indexTypes
			,int from, int offset
			,String sortField
			,String sortType
			){
		if(null == queryBuilder || offset <=0 || null == indexNames )
			return null;
		 this.logger.debug("dada"+ queryBuilder.toString());
		try{
			this.ESClient.admin().cluster().prepareClusterStats().execute().actionGet();
			SearchRequestBuilder searchRequestBuilder = this.ESClient.prepareSearch(indexNames).setTypes(indexTypes)
					.setSearchType(SearchType.DEFAULT).setQuery( queryBuilder).setFrom(from)
					.setSize(offset).setExplain(true);
			 if (sortField == null || sortField.isEmpty() 
					 || sortType == null || sortType.isEmpty()) {
				 /* 不需要排序*/
				 }
			 else {
				 /*需要排序*/
//				 org.elasticsearch.search.sort.SortOrder sortOrder = sortType.equals("desc") ? 
//						 org.elasticsearch.search.sort.SortOrder.DESC 
//						 : org.elasticsearch.search.sort.SortOrder.ASC;	 
//				 searchRequestBuilder = searchRequestBuilder.addSort(sortField, sortOrder);
			 	}
//			 searchRequestBuilder = this.createHighlight(searchRequestBuilder, searchContentMap);
			 this.logger.debug(searchRequestBuilder.toString());
			 SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();			
			 return  searchResponse; 
		}
		catch(Exception e){
    		 this.logger.error(e.getMessage());
    	}
		
		return null;
	}
	
	
	public boolean insert(List<XContentBuilder> dataList,
			String index_name, String index_type){
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
	
//	public boolean insert(String indexName,
//			HashMap<String, Object[]> insertContentMap) {
//		// TODO Auto-generated method stub
//		 XContentBuilder xContentBuilder = null;
//		 try {
//			 xContentBuilder = XContentFactory.jsonBuilder().startObject();
//		 }
//		 catch (IOException e) {
//			 this.logger.error(e.getMessage());
//			   return false;
//		 }
//		  Iterator<Entry<String, Object[]>> iterator = insertContentMap.entrySet().iterator();
//		  while (iterator.hasNext()) {
//			  Entry<String, Object[]> entry = iterator.next();
//			  String field = entry.getKey();
//			  Object[] values = entry.getValue();
//			  String formatValue = this.formatInsertData(values);
//			  try {
//				  xContentBuilder = xContentBuilder.field(field, formatValue);
//			  }
//			  catch (IOException e) {
//				  this.logger.error(e.getMessage());
//				   return false;
//			  }
//		  }
//		  try {
//			  xContentBuilder = xContentBuilder.endObject();
//		  }
//		  catch (IOException e) {
//			  this.logger.error(e.getMessage());
//			  return false;
//		  }
//		  return this._bulkInsertData(indexName, xContentBuilder);
//	}
//	
//	private String formatInsertData(Object[] values) {
//        if (!this.checkValue(values)) {
//            return "";
//        }
//        if (MySearchOption.isDate(values[0])) {
//            this.logger.warn("[" + values[0].toString() + "] formatDate");
//            return MySearchOption.formatDate(values[0]);
//        }
//        String formatValue = values[0].toString();
//        for (int index = 1; index < values.length; ++index) {
//            formatValue += "," + values[index].toString();
//        }
//        return formatValue.trim();
//    }
//	private boolean checkValue(Object[] values) {
//		// TODO Auto-generated method stub
//			if(values == null)
//				return false;
//			else if(values.length == 0)
//				return false;
//			else if(values[0] == null)
//				return false;
//			else if(values[0].toString().trim().isEmpty())
//				return false;
//		return true;
//		}
	

	public boolean deleteByQuery(String[] indexName,String[] indexTypes,QueryBuilder queryBuilder){
		try{	
			if(null ==queryBuilder|| null == indexName || null == indexTypes)
				return false;
			this.logger.warn("[" + indexName + "]" + queryBuilder.toString());
			this.ESClient.prepareDeleteByQuery(indexName).setTypes(indexTypes)
			.setQuery(queryBuilder).execute().actionGet();		
			return true;
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
		return false;
	}
	
//	public boolean updateByQuery(String indexName,String indexType
//			,HashMap<String, Object[]> newContentMap
//			,QueryBuilder queryBuilder
//			,int from,int offset)
//	{
//		try{
//			List<Map<String ,Object>> searchResult = this.query(queryBuilder, indexNames, indexTypes, from,  offset, null, null);
//			if (searchResult == null || searchResult.size() == 0) {
//				  //this.logger.warn("未找到需要更新的数据");
//				  return false;
//			}
//			if (!this.deleteByQuery(indexNames,indexTypes,queryBuilder)) {
//				//this.logger.warn("删除数据失败");
//				 return false;
//			}
//			HashMap<String, Object[]> insertContentMap = new HashMap<String, Object[]>();
//			for (Map<String, Object> contentMap : searchResult) {
//				Iterator<Entry<String, Object>> oldContentIterator = contentMap.entrySet().iterator();
//				 while (oldContentIterator.hasNext()) {
//					 Entry<String, Object> entry = oldContentIterator.next();
//					 insertContentMap.put(entry.getKey(), new Object[] { entry.getValue() });
//				 }
//			}
//			Iterator<Entry<String, Object[]>> newContentIterator = newContentMap.entrySet().iterator();
//			while (newContentIterator.hasNext()) {
//				 Entry<String, Object[]> entry = newContentIterator.next();
//				 insertContentMap.put(entry.getKey(), entry.getValue());
//			}
//			return this.bulkInsertData(indexName, insertContentMap);
//		}
//		catch(Exception e)
//		{
//			this.logger.error(e.getMessage());
//		}
//		return false;
//	}

	public boolean deleteIndexByName(String[] indexNames)
	  {		 try{
				  DeleteIndexResponse delete = this.ESClient
						  .admin().indices().delete(new DeleteIndexRequest(indexNames)).actionGet();
				  if (!delete.isAcknowledged()) {
					   this.logger.error(indexNames.toString()+ " wasn't deleted");
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

//		public boolean creatIndexTemplate(String template
//				,String indexTemplateName
//				,String indexType)
//		{
//					try{		
//						 PutIndexTemplateResponse putIndexTemplateResponse =  
//								 this.ESClient.admin().indices()
//								 .preparePutTemplate(indexTemplateName)
//								 .setSource(template).execute().actionGet();
//						if(putIndexTemplateResponse.isAcknowledged()){
//							return true;
//						}
//						else{
//							this.logger.error("创建索引失败");
//							return false;
//						}					
//					}
//					catch(Exception e){			
//						this.logger.error(e.getMessage());
//						return false;
//					}
//		}

	

	
	
}
