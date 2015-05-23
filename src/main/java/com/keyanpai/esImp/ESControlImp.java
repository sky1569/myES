package com.keyanpai.esImp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.joda.time.DateTime;





import com.keyanpai.esInterface.ESControl;
import com.keyanpai.instance.MySearchOption;
import com.keyanpai.instance.MySearchOption.SearchLogic;

public class ESControlImp implements ESControl{

	/*类初始化*/
	private static ESControlImp esControlImp;
	private Logger logger = Logger.getLogger(ESControl.class);			
	private Client ESClient = null;
	
	private ESControlImp(){
		PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
	}
	public static synchronized ESControlImp getInstance(){
		if(esControlImp == null)
			esControlImp = new ESControlImp();
		return esControlImp;
	}
	
	public boolean controlConfigure(Client esClient) {
		// TODO Auto-generated method stub
		try{
			this.ESClient = esClient;
			return true;
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
		return false;
	}
	
	
	
	private ESSearchImp esSearchImp;
	public void setESSeachImp(ESSearchImp esSearchImp)
	{
		this.esSearchImp = esSearchImp;
		this.esSearchImp.searchConfigure(ESClient);
	}
	
	
	
	public boolean bulkInsert(List<XContentBuilder> dataList, String index_id,
			String index_name, String index_type) {
		// TODO Auto-generated method stub
		return this._bulkInsertData(dataList, index_id, index_name, index_type) ;
		
	}

	private boolean _bulkInsertData(List<XContentBuilder> dataList,
			String index_id, String index_name, String index_type) {
		// TODO Auto-generated method stub
		try{
    		BulkRequestBuilder bulk = this.ESClient.prepareBulk();
    		for(XContentBuilder xc : dataList)
    		{
    			DateTime dataTime = new DateTime();

    			String dateFormat = dataTime.getYear()
    					+ "-"
    					+ (dataTime.getMonthOfYear() < 10 ? ("0" + dataTime
    							.getMonthOfYear()) : dataTime.getMonthOfYear())
    					+ "-"
    					+ (dataTime.getDayOfMonth() < 10 ? ("0" + dataTime
    							.getDayOfMonth()) : dataTime.getDayOfMonth());
    			String new_indexName = index_name + "-" + index_id + "-" + dateFormat;
    			IndexRequest request = new IndexRequest(new_indexName, index_type);
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
			QueryBuilder queryBuilder = null;
			queryBuilder = this.esSearchImp.createQueryBuilder(contentMap, SearchLogic.must);
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

	public boolean bulkUpdate(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub
		try{
			List<Map<String ,Object>> searchResult = this.esSearchImp.simpleSearch(
					new String[]{indexName}, oldContentMap, null, 0,1,null, null);
			if (searchResult == null || searchResult.size() == 0) {
				  this.logger.warn("未找到需要更新的数据");
				  return false;
			}
			if (!this.bulkDeleteData(indexName, oldContentMap)) {
				this.logger.warn("删除数据失败");
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
			queryBuilder = this.esSearchImp.createQueryBuilder(contentMap, SearchLogic.must);
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
		  try {
			  this.logger.debug("[" + indexName + "]" + xContentBuilder.string());
		  }
		  catch (IOException e) {
			  this.logger.error(e.getMessage());
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
	
	
	
	
	
	public boolean update(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap) {
		// TODO Auto-generated method stub
		return false;
	}

}
