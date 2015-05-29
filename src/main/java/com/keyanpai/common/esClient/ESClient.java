package com.keyanpai.common.esClient;

import java.util.List;

import org.elasticsearch.client.Client;

public abstract class ESClient {
	public Client ESClient = null;
	public List<String> clusterList;
	public abstract boolean clientConn() ;
	public abstract boolean clientDestroy();
	public Client getClient(){System.out.println("abstracts");if(this.ESClient == null) System.out.println("abstracts is null");return this.ESClient; };
}
