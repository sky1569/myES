package com.keyanpai.es.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.Nullable;

import com.keyanpai.es.MySearchOption.SearchLogic;

public interface ESStat {

		boolean statConfigure(
			List<String> clusterList,String[] indexName,String indexType);
		List<Map<String, Object>> simpleStat(String[] indexNames
	            , HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic
	            , @Nullable HashMap<String, Object[]> filterContentMap, @Nullable SearchLogic filterLogic
	            , int from, int offset, @Nullable String sortField, @Nullable String sortType);
}
