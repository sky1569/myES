package com.keyanpai.servlet.account;


import com.keyanpai.common.esClient.ESClient;
import com.keyanpai.dao.esStats.ESStatsImp;

public class UserAccount extends GuestAccount implements AccountStatsInterface {
	public ESStatsImp esStatsImp = new ESStatsImp(esClient.getClient());
	
	public UserAccount(String id
					   ,String ip
					   ,String name
					   ,String password
					   ,ESClient es)
	{ 
		super(es);
		this.setId(id);
		this.setIp(ip);
		this.setName(name);
		this.setPassword(password);
		this.setAccountType();		
	}		

	public double getMin(String fieldName) {
		// TODO Auto-generated method stub		
		return esStatsImp.getMin( fieldName);		
	}

	public long getCount(String fieldName) {
		// TODO Auto-generated method stub		
		return esStatsImp.getCount(fieldName);	
	}
	
	public double getAvg(String fieldName) {
		// TODO Auto-generated method stub
			
		return esStatsImp.getAvg(fieldName);		
	}



}
