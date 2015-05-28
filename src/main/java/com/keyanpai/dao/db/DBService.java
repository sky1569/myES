package com.keyanpai.dao.db;


import com.keyanpai.common.DBConfigure;
import com.keyanpai.dao.esControl.ESControlImp;

public interface DBService {
	boolean DBSetter(DBConfigure dbConfigure);
	boolean handlData(ESControlImp esControlImp);
}
