package com.keyanpai.dao.esClient;

import java.util.List;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


public class ESClientImp extends ESClient{
	
	//private Logger logger = Logger.getLogger(ESClientImp.class);
	
	
	public void ESClientConfigure(List<String> clusterList)
	{
		try{
		//	PropertyConfigurator.configure(ResourceLoader.loadLog4jProperties("log4j.properties")) ;
			this.clusterList = clusterList;
		}
		catch(Exception e){
			//this.logger.error(e.getMessage());
			System.out.println("3333");
		}
	}
	
	
	
	/*连接关闭操作*/
	public boolean  clientConn()  {
   //     this.logger.info("连接搜索服务器"); 
         return this._open();
     
   }	
	private boolean _open() {
		// TODO Auto-generated method stub
			 /*100s没有链接上搜索服务器，则超时*/
		try{	
			 Settings settings = ImmutableSettings.settingsBuilder()
					// .put(this.ESClientConfigureMap)
						.put("client.transport.ping_timeout", 100)
						.put("cluster.name", "elasticsearch").build();
			 /*创建搜素客户端*/	
			 this.ESClient = new TransportClient(settings);
			 //debug 判断是否为空
			 for(String item : this.clusterList)
			 {
				 String address = item.split(":")[1];
				 int port = Integer.parseInt(item.split(":")[2]);
				 System.out.println(item);
				 /*通过tcp连接搜索服务器，如果连接不上，有一种可能是服务器端与客户端的jar包版本不匹配*/
				 this.ESClient = ((TransportClient) this.ESClient)
						 .addTransportAddress(new InetSocketTransportAddress(address, port));
			 }
		//	 this.logger.info("连接完成");
			 return true;
		}
		catch(Exception e){
		//	this.logger.error("连接错误");
			//this.logger.error(e.getMessage());
		}
			return false;
	}			 
	
	public boolean clientDestroy() {
       // this.logger.info("关闭搜索客户端");
        return this._close();
    }

	private boolean _close(){
	       if (this.ESClient == null) {return true;}
	     try{
		       this.ESClient.close();
		       this.ESClient = null;
		     	return true;
	     	}
	     catch(Exception e)
	     {
	    //	 this.logger.error(e.getMessage());
	     }
	     return false;
	}	
	
	
	public Client getClient()
	{	
		//是不是需要一个ｃｌｉｅｎｔ池
		return this.ESClient;
	}



}
