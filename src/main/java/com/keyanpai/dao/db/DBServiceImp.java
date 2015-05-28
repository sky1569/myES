package com.keyanpai.dao.db;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.joda.time.DateTime;

import com.keyanpai.common.DBConfigure;
import com.keyanpai.dao.esControl.ESControlImp;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBServiceImp implements DBService{
	private static DBServiceImp ds = null;
	private DBConfigure dbC;
	private Connection conn;
	private Statement stmt,stmt2,stmtMax;
	private Logger logger = Logger.getLogger(DBServiceImp.class);
	//private int maxRecoder = 4000;

	public static DBServiceImp  getDBServiceImp()	{
		if(ds == null)
		{			
			ds = new DBServiceImp();
		}
		return ds;
	}

	private DBServiceImp(){
		
	//	PropertyConfigurator.configure("/home/sky/workspace/com.D-media.keyanpai/log4j.properties") ;
	}

	public boolean DBSetter(DBConfigure dbConfigure) {
		// TODO Auto-generated method stub	
		try{
			this.dbC = dbConfigure;
			return true;
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		
		return false;
	}
	public boolean handlData(ESControlImp esControlImp) {
		// TODO Auto-generated method stub
		try
		{
			long tableRecoderCount = _getCountOfTable( this.dbC.getPriTableName());
		
			if (tableRecoderCount > 0) {
	
				// 分批次读取
				long queryPerid = (tableRecoderCount + this.dbC.getMaxRecoder() - 1) / this.dbC.getMaxRecoder();
				// 获取表结构
				LinkedHashMap<String, String> columns = _getTablesColums( this.dbC.getPriTableName());
				
				LinkedHashMap<String, Long> columnsComplex = new LinkedHashMap<String, Long>();
				if(!this.dbC.getTableComplexNames().isEmpty())
				{
					columnsComplex = _getTablesColums(this.dbC.getTableComplexNames());
				}
				else
				{
					columnsComplex = null;
				}
				for (int i = 0; i < queryPerid; i++) {	
					List<XContentBuilder> dataList = _getTablesData(
							this.dbC.getPriTableName(),this.dbC.getTableSimpleNames(),this.dbC.getTableComplexNames(),this.dbC.getUniqFieldName(), columns, i,columnsComplex);
						esControlImp.bulkInsert(dataList, this.dbC.getIndexName() ,this.dbC.getIndexType());
							System.out.println(i +":"+  DateTime.now());
							
				}
			}
			return true;
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}
		return false;
	}
	
	private List<XContentBuilder> _getTablesData(String priTableName,
			Map<String, String> tableSimpleNames,
			Map<String, String> tableComplexNames, String uniqFieldName,
			LinkedHashMap<String, String> columns, int offsets,
			LinkedHashMap<String, Long> columnsComplex) throws IOException, SQLException {
		// TODO Auto-generated method stub
		String sql = "select *  from "+ this.dbC.getDatabaseName() + "." + priTableName + " limit %s,%s";
		ResultSet rs = stmt.executeQuery(String.format(sql, offsets * this.dbC.getMaxRecoder(), this.dbC.getMaxRecoder()));
		List<XContentBuilder> result = new ArrayList<XContentBuilder>();
		String dataType = null;
		String colmnuName = null;
		while (rs.next()) {
			XContentBuilder xc = XContentFactory.jsonBuilder().startObject();
			for (Entry<String, String> colunm : columns.entrySet()) {
				dataType = colunm.getValue();
				colmnuName = colunm.getKey();
				if (dataType.equals("tinyint") || dataType.equals("smallint")) {					
					xc.field(colmnuName,rs.getInt(colmnuName));
				} else if (dataType.equals("mediumint")
						|| dataType.equals("integer")
						|| dataType.equals("bigint") || dataType.equals("int")
						|| dataType.equals("decimal")
						|| dataType.equals("numeric")) {
					xc.field(colmnuName,rs.getLong(colmnuName));
				} else if (dataType.equals("float")) {
					xc.field(colmnuName,rs.getFloat(colmnuName));
				} else if (dataType.equals("double")) {
					xc.field(colmnuName,rs.getDouble(colmnuName));
				}else if(dataType.equals("varchar"))
				{
					
					xc.field(colmnuName,rs.getString(colmnuName) == null ? "null":rs.getString(colmnuName));
				}    

			}			
	
			String uniqId = rs.getString(uniqFieldName);
			
			if(tableSimpleNames != null)
				for(Entry<String, String> s : tableSimpleNames.entrySet())
				{
					this._getSimpleTablesData(xc, s.getValue(), uniqFieldName,s.getKey(), uniqId);
				}
			if(tableComplexNames != null)
				for(Entry<String, String> s : tableComplexNames.entrySet())
				{
			
					this._getComplexTablesData(xc, s.getValue(), uniqFieldName, s.getKey(), uniqId,columnsComplex.get(s.getValue()));
				}
					
			xc.endObject();
			result.add(xc);
	}
		rs.close();
		return result;
	}

	private void _getComplexTablesData(XContentBuilder xc, String tableName ,
			String uniqFieldName, String feildName, String uniqId, Long columnsNum) {
		// TODO Auto-generated method stub
		String sql = "select *  from "+ this.dbC.getDatabaseName()
				+ "." + tableName +" where " + this.dbC.getDatabaseName() + "." + tableName + "." + uniqFieldName + " = \'" + uniqId +"\'";
		ResultSet rs;
		try {
			rs = stmt2.executeQuery(sql);			
			 Object[] ob = _getComplexArray(rs,feildName,  columnsNum);		
			xc.array(feildName,ob);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.logger.error(e.getMessage());
		}				

	}

	private Object[] _getComplexArray(ResultSet rs, String feildName,
			Long columnsNum) {
		// TODO Auto-generated method stub
		Object[] obj ;
		Vector<String> v = new Vector<String>();
	
		try {
			while (rs.next())
			{	
				StringBuilder ref = new StringBuilder();
				ref.append(rs.getString(1));
				for(int i = 2; i< columnsNum;i++)
				{
					 ref.append(",");
					 ref.append(rs.getString(i));
				}
		
				v.add(ref.toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.logger.error(e.getMessage());
		}			
		obj = v.toArray();
		
		return obj;
	}

	private void _getSimpleTablesData(XContentBuilder xc, String tableName,
			String uniqField, String feildName, String uniqId) {
		// TODO Auto-generated method stub
		String sql = "select *  from "+ this.dbC.getDatabaseName()
				+ "." + tableName +" where " + this.dbC.getDatabaseName() + "." + tableName + "." + uniqField + " = \'" + uniqId +"\'";
		ResultSet rs;
		try {
			rs = stmt2.executeQuery(sql);			
			 Object[] ob = _getSimpleArray(rs,feildName);		
			xc.array(feildName,ob);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.logger.error(e.getMessage());
		}				
		
	}

	private Object[] _getSimpleArray(ResultSet rs, String feildName) {
		// TODO Auto-generated method stub
		Object[] obj ;
		Vector<String> v = new Vector<String>();
		try {
			while (rs.next())
			{			
				v.add(rs.getString(3) == null ? "null":rs.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.logger.error(e.getMessage());
		}			
		obj = v.toArray();
		return obj;
	}

	private LinkedHashMap<String, String> _getTablesColums(String tableName) throws SQLException {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		String sql = "select COLUMN_NAME,DATA_TYPE FROM information_schema.COLUMNS where TABLE_NAME='"
				+ tableName + "' AND TABLE_SCHEMA='" + this.dbC.getDatabaseName() + "'";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			result.put(rs.getString(1), rs.getString(2).toLowerCase());
		}
		rs.close();
		return result;
	}

	private LinkedHashMap<String, Long> _getTablesColums(Map<String, String> tableComplexNames) throws SQLException {
		// TODO Auto-generated method stub
		LinkedHashMap<String, Long> result = new LinkedHashMap<String, Long>();
		for(Entry<String,String> tc : tableComplexNames.entrySet())
		{
			String sql = "select count(*) from information_schema.COLUMNS where TABLE_NAME='"
					+ tc.getValue() + "' AND TABLE_SCHEMA='" + this.dbC.getDatabaseName() + "'";
			System.out.println(sql);
			try {
				ResultSet rs = stmtMax.executeQuery(sql);
				while(rs.next())
				{
					result.put(tc.getValue(), rs.getLong(1));
					System.out.println(rs.getLong(1));
				}
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				this.logger.error(e.getMessage());
			}
		}
		return result;
	}

	private long _getCountOfTable(String tableName) throws SQLException 
	{
		// TODO Auto-generated method stub
		String sql = "select count(1) from " + this.dbC.getDatabaseName() + "." + tableName;
	
		ResultSet rs = stmtMax.executeQuery(sql);
		long result = 0;
		while (rs.next()) {
			result = rs.getLong(1);
		}
		rs.close();
		return result;		
	}

	public void open() {
		// TODO Auto-generated method stub
		
		try{
			//建立数据库链接
			 conn = _getConnection();
			 //主表查询操作
			 stmt = (Statement) conn.createStatement();
			 //辅表查询操作
			 stmt2 = (Statement) conn.createStatement();
			 //复杂辅表字段个数操作
			 stmtMax = (Statement) conn.createStatement();
		}
		catch(Exception e)
		{
			this.logger.error(e.getMessage());
		}

	}

	private Connection _getConnection() {
		// TODO Auto-generated method stub
		Connection conn = null;
		String url = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			this.logger.error(e.getMessage());
		}
		try {
			url = "jdbc:mysql://"
					+ dbC.getHost()
					+ "/"
					+ dbC.getDatabaseName()
					+ "?user="
					+ dbC.getUserName()
					+ "&password="
					+ dbC.getPassword()
					+ "&useUnicode=true&&characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=convertToNull";// 简单写法：url
			conn = (Connection) DriverManager.getConnection(url, dbC.getUserName(),
					dbC.getPassword());
		} catch (SQLException e) {
			this.logger.error(e.getMessage());
		}
		return conn;	
	}

	public void close() {
		// TODO Auto-generated method stub
		try {
			if(!this.stmt.isClosed())
				this.stmt.close();
			if(!this.stmt2.isClosed())
				this.stmt2.close();
			if(!this.stmtMax.isClosed())
				this.stmtMax.close();
			if(!this.conn.isClosed())
				this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.logger.error(e.getMessage());
		}
		
		
	}

}
