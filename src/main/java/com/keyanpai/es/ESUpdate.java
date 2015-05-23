package com.keyanpai.es;

import java.util.HashMap;

public interface ESUpdate {
	boolean update(
			String indexName ,HashMap<String ,Object[]>oldContentMap,HashMap<String,Object[]>newContentMap);
}
