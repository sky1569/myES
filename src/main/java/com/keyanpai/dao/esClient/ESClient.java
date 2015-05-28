package com.keyanpai.dao.esClient;

import java.util.List;

import org.elasticsearch.client.Client;

public abstract class ESClient {
	public Client ESClient = null;
	public List<String> clusterList;
	public abstract void ESClientConfigure(List<String> clusterList);
	public abstract boolean clientConn() ;
	public abstract boolean clientDestroy();
	public Client getClient() {return this.ESClient;}
}
