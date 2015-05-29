package com.keyanpai.dao.esSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.elasticsearch.common.Nullable;

import com.keyanpai.common.MySearchOption.SearchLogic;

public abstract class ESSearch {
	
	
	abstract long getCount(String[] indexNames,HashMap<String,Object[]> searchContentMap
			,SearchLogic searchLogic,@Nullable HashMap<String,Object[]> filterContentMap
			,@Nullable SearchLogic filterLogic);
	abstract List<Map<String ,Object> > getSuggest(
			String[] indexNames,String filedName,String value,int count);
	abstract  Map<String, String> group(String indexName
            , HashMap<String, Object[]> mustSearchContentMap
            , HashMap<String, Object[]> shouldSearchContentMap
            , String[] groupFields);
}
