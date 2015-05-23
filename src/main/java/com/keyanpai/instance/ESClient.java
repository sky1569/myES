package com.keyanpai.instance;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


public class ESClient {

	private Client ESClient = null;
	/*设置参数*/
	private List<String> clusterList = null;
//	private String[] indexName;
//	private String[] indexType;	
	private Logger logger = Logger.getLogger(ESClient.class);	
	public ESClient(List<String> clusterList
			//,	String[] indexName, String[] indexType
			){		
		
		try{
			PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
			this.clusterList = clusterList;
//			this.indexName = indexName;
//			this.indexType = indexType;			
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
	}

	/*连接关闭操作*/
	public boolean  clientConn()  {
        this.logger.info("连接搜索服务器");	    	
        
        try{
        	this._open();
        	  return true;
        }
        catch(Exception e){
        	this.logger.error(e.getMessage());
        }
       return false;
   }	

	private void _open() throws Exception {
		// TODO Auto-generated method stub
			 /*100s没有链接上搜索服务器，则超时*/
			 Settings settings = ImmutableSettings.settingsBuilder()
					// .put(this.ESClientConfigureMap)
						.put("client.transport.ping_timeout", 100)
						.put("cluster.name", "elasticsearch").build();
			 /*创建搜素客户端*/
			 this.ESClient = new TransportClient(settings);

			 //debug 判断是否为空
			 if(this.clusterList.isEmpty())
				 {
				 //本机和10.107.6.82是一个集群
				 this.logger.error("this.clusterList.isEmpty()");
				 }
			 
			 for(String item : this.clusterList)
			 {
				 String address = item.split(":")[0];
				 int port = Integer.parseInt(item.split(":")[1]);
				 /*通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配*/
				 this.ESClient = ((TransportClient) this.ESClient)
						 .addTransportAddress(new InetSocketTransportAddress(address, port));
			 }
		 }			 
	
	public void destroy() {
        this.logger.info("关闭搜索客户端");
        try {
			this._close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.logger.error(e.getMessage());
		}
    }

	private void _close()  throws Exception{
	        if (this.ESClient == null) {
	           return;
	        }
	       this.ESClient.close();
	       this.ESClient = null;
	}
	
	
	
	public Client getESClient()
	{
	
		return this.ESClient;
	}
}
