package com.keyanpai.dbInterface;


import com.keyanpai.esImp.ESControlImp;
import com.keyanpai.instance.DBConfigure;

public interface DBService {
	boolean DBSetter(DBConfigure dbConfigure);
	boolean handlData(ESControlImp esControlImp);
}
