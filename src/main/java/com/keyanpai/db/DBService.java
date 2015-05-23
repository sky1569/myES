package com.keyanpai.db;


import com.keyanpai.es.ESControlImp;

public interface DBService {
	boolean DBSetter(DBConfigure dbConfigure);
	boolean handlData(ESControlImp esControlImp);
}
