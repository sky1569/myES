package com.keyanpai.account;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import com.keyanpai.es.stat.ESStatImp;
import com.keyanpai.es.stat.ESStatInterface;



public class UserAccount extends GuestAccount /*implements ESStatInterface*/ {
	
	private Logger logger = Logger.getLogger(UserAccount.class);
		
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
		PropertyConfigurator.configure("../com.D-media.keyanpai/log4j.properties") ;
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
