package com.keyanpai.instance;

import java.util.Map;

public class DBConfigure {
	private int maxRecoder;
	public int getMaxRecoder() {
		return maxRecoder;
	}
	public void setMaxRecoder(int maxRecoder) {
		this.maxRecoder = maxRecoder;
	}
	private String host;
	private String userName;
	private String password;
	private String databaseName;
	private String priTableName;
	private Map<String,String> tableSimpleNames;
	private Map<String,String> tableComplexNames;
	private String uniqFieldName;
	
	public String getUniqFieldName() {
		return uniqFieldName;
	}
	public void setUniqFieldName(String fieldName) {
		this.uniqFieldName = fieldName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getPriTableName() {
		return priTableName;
	}
	public void setPriTableName(String priTableName) {
		this.priTableName = priTableName;
	}
	public Map<String, String> getTableSimpleNames() {
		return tableSimpleNames;
	}
	public void setTableSimpleNames(Map<String, String> tableSimpleNames) {
		this.tableSimpleNames = tableSimpleNames;
	}
	public Map<String, String> getTableComplexNames() {
		return tableComplexNames;
	}
	public void setTableComplexNames(Map<String, String> tableComplexNames) {
		this.tableComplexNames = tableComplexNames;
	}
}
