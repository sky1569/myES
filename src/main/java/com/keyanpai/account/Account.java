package com.keyanpai.account;

public class Account {

	private String id;
	private String ip;
	private String name;
	private String password;
	private type accountType;
	
	public enum type{
		admin,
		user,
		guest
	}
    
	public type getAccountType() {
		return accountType;
	}
	public void setAccountType() {
		this.accountType = _check();
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void search()
	{
		System.out.println(this.getName()+":search!");		
	}	

	private type _check() {
		// TODO Auto-generated method stub
		System.out.println("type _check():"+this.getId());		
		return type.admin;
	}
}
