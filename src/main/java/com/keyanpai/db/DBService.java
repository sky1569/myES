package com.keyanpai.db;


import com.keyanpai.es.control.ESControlImp;

public interface DBService {
	boolean DBSetter(DBConfigure dbConfigure);
	boolean handlData(ESControlImp esControlImp);
}
