package com.keyanpai.model;

import java.util.HashMap;
import java.util.Map;

public class ESContent {
  //es存储内容
		private String databaseName ;
		private String priTableName;
		private String uniqFieldName ; 		
		private Map<String,String> tableSimple = new HashMap<String,String>();
		private Map<String,String> tableComplex = new HashMap<String,String>();
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
		public String getUniqFieldName() {
			return uniqFieldName;
		}
		public void setUniqFieldName(String uniqFieldName) {
			this.uniqFieldName = uniqFieldName;
		}
		public Map<String, String> getTableSimple() {
			return tableSimple;
		}
		public void setTableSimple(Map<String, String> tableSimple) {
			this.tableSimple = tableSimple;
		}
		public Map<String, String> getTableComplex() {
			return tableComplex;
		}
		public void setTableComplex(Map<String, String> tableComplex) {
			this.tableComplex = tableComplex;
		}
		
}
