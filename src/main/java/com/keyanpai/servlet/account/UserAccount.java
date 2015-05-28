package com.keyanpai.servlet.account;


import com.keyanpai.dao.esStats.ESStatsImp;

public class UserAccount extends GuestAccount /*implements ESStatInterface*/ {
	

	public UserAccount(){}
	public UserAccount(String id
					   ,String ip
					   ,String name
					   ,String password
					   )
	{
		this.setId(id);
		this.setIp(ip);
		this.setName(name);
		this.setPassword(password);
		this.setAccountType();
		
	}		

	public double getMin(String fieldName) {
		// TODO Auto-generated method stub
		ESStatsImp esStatsImp = new ESStatsImp(esClient.getClient());	
		return esStatsImp.getMin( fieldName);		
	}

	public long getCount(String fieldName) {
		// TODO Auto-generated method stub
		ESStatsImp esStatImp = new ESStatsImp(esClient.getClient());	
		return esStatImp.getCount(fieldName);	
	}
	
	public double getAvg(String fieldName) {
		// TODO Auto-generated method stub
		ESStatsImp esStatImp = new ESStatsImp(esClient.getClient());	
		return esStatImp.getAvg(fieldName);		
	}



}
