package com.keyanpai.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

import com.keyanpai.common.MySearchOption.SearchLogic;


public class ESCreatQueryBuilder {
	private Logger logger = Logger.getLogger("DAO.ESCreatQueryBuilder");	
	public QueryBuilder createQueryBuilder(
			HashMap<String, Object[]> searchContentMap, SearchLogic searchLogic) {
		// TODO Auto-generated method stub
		try{
			if(searchContentMap == null || searchContentMap.size() == 0){
				return null;				
			}
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			Iterator<Entry<String,Object[]>> iterator = searchContentMap.entrySet().iterator();
			/*循环每一个需要搜索的字段和值*/
			while(iterator.hasNext()){
				Entry<String,Object[]> entry = iterator.next();
				String field =entry.getKey();			
				Object[] values = entry.getValue();		
				/*排除非法的搜索值*/
				if(!this.checkValue(values)){
					continue;
				}
				MySearchOption mySearchOption = this.getSearchOption(values);			
				
				QueryBuilder queryBuilder = this.createSingleFieldQueryBuilder(
						field, values, mySearchOption);
				if(queryBuilder != null)				{
					if(searchLogic == SearchLogic.should){
						boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
					}
					else{
						boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
					}
				}
			}
			return boolQueryBuilder;
		}
		catch(Exception e){
			this.logger.error(e.getMessage());
		}
			
		return null;
	}

	private QueryBuilder createSingleFieldQueryBuilder(String field,
			Object[] values, MySearchOption mySearchOption) {
		// TODO Auto-generated method stub
		try{
				if(mySearchOption.getSearchType() 
						== com.keyanpai.common.MySearchOption.SearchType.range)
					return this.createRangeQueryBuilder(field,values);
				BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
				 for (Object valueItem : values) {
					 if (valueItem instanceof MySearchOption) {
						 continue;
						 
					 }else System.out.println(valueItem);
					 QueryBuilder queryBuilder = null;
					 String formatValue = valueItem.toString().trim().replace("*", "");//格式化搜索数据
			
					 if (mySearchOption.getSearchType() 
							 == com.keyanpai.common.MySearchOption.SearchType.term) {
						 queryBuilder = QueryBuilders.termQuery(field, formatValue)
								 .boost(mySearchOption.getBoost());
					 }
					 else if (mySearchOption.getSearchType() 
							 == com.keyanpai.common.MySearchOption.SearchType.querystring) {
		//				 if (formatValue.length() == 1) {
		//					 /*如果搜索长度为1的非数字的字符串，格式化为通配符搜索，暂时这样，以后有时间改成multifield搜索，就不需要通配符了*/
		//					 if (!Pattern.matches("[0-9]", formatValue)) {
		//						 formatValue = "*"+formatValue+"*";							
		//					 }
		//				 }					
						 QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(formatValue).analyzer("ik")
								 .minimumShouldMatch(mySearchOption.getQueryStringPrecision());
						 queryBuilder = queryStringQueryBuilder.field(field).boost(mySearchOption.getBoost());
						 
					 }
					 if (mySearchOption.getSearchLogic() == SearchLogic.should) {
						   boolQueryBuilder = boolQueryBuilder.should(queryBuilder);
					 }
					 else{
						 boolQueryBuilder = boolQueryBuilder.must(queryBuilder);
					 }
				 }
				return boolQueryBuilder;
			}
			catch(Exception e)
			{
				this.logger.error(e.getMessage());
			}
		return null;
				
	}

	private QueryBuilder createRangeQueryBuilder(String field, Object[] values) {
		// TODO Auto-generated method stub
		if(values.length == 1 || values[1] == null || values[1].toString().trim().isEmpty()){
			this.logger.warn("[区间搜索]必须传递两个值，但是只传递了一个值，所以返回null");
			return null;
		}
		boolean timeType = false;
		if(MySearchOption.isDate(values[0]))
		{
			if(MySearchOption.isDate(values[1]))
			{
				timeType = true;
			}
		}
		String begin = "",end = "";
		if(timeType)
		{
			begin = MySearchOption.formatDate(values[0]);
			
			end = MySearchOption.formatDate(values[1]);
		}
		else
		{
			 begin = values[0].toString();
		
			 end = values[1].toString();
		
		}
		return QueryBuilders.rangeQuery(field).from(begin).to(end);
	}

	private MySearchOption getSearchOption(Object[] values) {
		// TODO Auto-generated method stub
		try{
			 for(Object item : values) {
				 if(item instanceof MySearchOption) {
					 return (MySearchOption) item;
				 }
			 }
		 }
		 catch(Exception e){
			 this.logger.error(e.getMessage());
		 }
		 return new MySearchOption();
	}

	private boolean checkValue(Object[] values) {
		// TODO Auto-generated method stub
		if(values == null)
			return false;
		else if(values.length == 0)
			return false;
		else if(values[0] == null)
			return false;
		else if(values[0].toString().trim().isEmpty())
			return false;
	return true;
	}
}
