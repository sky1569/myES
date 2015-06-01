package com.keyanpai.service.esSearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.keyanpai.common.MySearchOption.SearchLogic;

public interface ESSearchInterface {
	

	/**
	 * 
	 * @param indexNames
	 * @param indexTypes
	 * @param searchContentMap
	 * @param searchLogic
	 * @param filterContentMap
	 * @param filterLogic
	 * @param from
	 * @param offset
	 * @param sortField
	 * @param sortType
	 * @return
	 */

	 List<Map<String, Object>> simpleSearch(String[] indexNames,String[] indexTypes
				,HashMap<String, Object[]> searchContentMap
				,SearchLogic searchLogic
				,HashMap<String, Object[]> filterContentMap
				,SearchLogic filterLogic
				,int from, int offset
				,String sortField
				,String sortType);
	
	/**
	 * 
	 * @param indexNames
	 * @param indexTypes
	 * @param searchContentMap
	 * @param searchLogic
	 * @param filterContentMap
	 * @param filterLogic
	 * @param from
	 * @param offset
	 * @param sortField
	 * @param sortType
	 * @return
	 */
	
	 long getCount(String[] indexNames,String[] indexTypes
				,HashMap<String, Object[]> searchContentMap
				,SearchLogic searchLogic
				,HashMap<String, Object[]> filterContentMap
				,SearchLogic filterLogic
			);
//	/**
//	 * 
//	 * @param indexNames//	 * @param filedName
//	 * @param value
//	 * @param count
//	 * @return
//	 */
//	List<Map<String ,Object> > getSuggest(String[] indexNames
//			,String filedName
//			,String value
//			,int count); 
//	/**
//	 * 
//	 * @param indexName
//	 * @param mustSearchContentMap
//	 * @param shouldSearchContentMap
//	 * @param groupFields
//	 * @return
//	 */
//    Map<String, String> group(String indexName
//	            , HashMap<String, Object[]> mustSearchContentMap
//	            , HashMap<String, Object[]> shouldSearchContentMap
//	            , String[] groupFields);
}
