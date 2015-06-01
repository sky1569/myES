package com.keyanpai.service.account;



import com.keyanpai.service.esStats.ESStatsImp;

public class UserAccount extends GuestAccount implements AccountStatsInterface {
	public ESStatsImp esStatsImp = null;
	
	public UserAccount(){		
//		BeanFactory beanFactory = new ClassPathXmlApplicationContext("beans.xml");
//		esStatsImp  =  beanFactory.getBean("esStatsImp",ESStatsImp.class );
		}
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
