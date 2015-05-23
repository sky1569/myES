package com.keyanpai.account;

public class account {
	private String id;
	private String ip;
	private String name;
	private String password;

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
	
	public void up(){
		System.out.println(this.getName()+":up!");
	}
	public void down(){
		System.out.println(this.getName()+":down!");
	}
	public void comment(){
		System.out.println(this.getName()+":comment!");
	}
	public void search()
	{
		System.out.println(this.getName()+":search!");		
	}
	public void update()
	{
		System.out.println(this.getName()+":update!");		
	}
}