package com.keyanpai.account;


import com.keyanpai.es.stat.ESStatImp;

public class UserAccount extends GuestAccount /*implements ESStatInterface*/ {
	

		
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
		ESStatImp esStatImp = new ESStatImp(esClient.getClient());	
		return esStatImp.getMin( fieldName);		
	}

	public long getCount(String fieldName) {
		// TODO Auto-generated method stub
		ESStatImp esStatImp = new ESStatImp(esClient.getClient());	
		return esStatImp.getCount(fieldName);	
	}
	
	public double getAvg(String fieldName) {
		// TODO Auto-generated method stub
		ESStatImp esStatImp = new ESStatImp(esClient.getClient());	
		return esStatImp.getAvg(fieldName);		
	}



}
