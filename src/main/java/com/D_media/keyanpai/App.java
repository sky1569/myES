package com.D_media.keyanpai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.keyanpai.account.AdminAccount;
import com.keyanpai.db.DBConfigure;
import com.keyanpai.db.DBServiceImp;
import com.keyanpai.es.MySearchOption;
import com.keyanpai.es.MySearchOption.DataFilter;
import com.keyanpai.es.MySearchOption.SearchLogic;
import com.keyanpai.es.MySearchOption.SearchType;
import com.keyanpai.es.client.ESClientImp;
import com.keyanpai.es.control.ESControlImp;
import com.keyanpai.es.search.ESSearchImp;
import com.keyanpai.escontent.CnkiContent;




/**
 * Hello world!
 *
 */
public class App 
{
	
    public static void main( String[] args ) 
    {
        System.out.println( "!!!!!!!!!!!!!Hello World!!!!!!!!!!!!!!!!!" );
        System.out.println( "!!!!!!!!!!!!!Hello World!!!!!!!!!!!!!!!!!" );
        AdminAccount aa = new AdminAccount("1","localhost","sky","1206");
          List<String> clusterList = new ArrayList<String>();
          clusterList.add("10.107.6.82:9300");
          aa.getClientConn(clusterList);
              // ESSearch(aa);

              ESDelete(aa);
           //   ESInsert(aa);
            //  ESUpdate(aa);
               aa.getClientClosed();
    }
    
    
    private static void ESUpdate(final AdminAccount aa){
    	
    	final  String[] indexNames = new String[] {     	        	
    	        		 "cnki-1-2015-05-23"    	        	
    	        		};
    	final String indexType = "paper";
    	  String[] value = new String[] {"北京邮电大学","倪尧天","estest"};
    	  String field = "keywords_name";
    	  final String _id = "AU1_NpqJmPZGBB7kwM95";
    	  final  HashMap<String,Object[]> newContentMap = new HashMap<String,Object[]>();
    	  newContentMap.put(field, value);
    	
	  
    	  new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				aa.getAccountType();
			}
    		  
    	  }).start(); 
    }
    
    private static void ESDelete(  AdminAccount aa){    	
    
    	  String[] indexNames = new String[] {     	        	
    	        		 "cnki"    	        	
    	        		};
    	 String[] value = new String[]{"早期"};
    	 HashMap<String,Object[]> contentMap = new HashMap<String,Object[]>();
    	 contentMap.put("name",value);  
    	System.out.println(aa.bulkDelete(indexNames, contentMap)); 
    	
    }
    private static void ESInsert( AdminAccount aa){   	 

    	String host = "10.105.223.24";
 		String userName = "root";
 		String password = "buptmitc"; 		
 		int maxRecoder = 4000;
 		
 		String databaseName = "cnki_2015_0419";
 		String priTableName = "tb_cnki";
 		String uniqFieldName = "uniq_id"; 
 		
 		String indexType = "paper";
 		String indexId = "2";
 		String indexTime = "data-missing";
 		String indexName = "cnki" + "-" + indexTime +"-"+indexId ;
//		DateTime dataTime = new DateTime();
//
//		String dateFormat = dataTime.getYear()
//				+ "-"
//				+ (dataTime.getMonthOfYear() < 10 ? ("0" + dataTime
//						.getMonthOfYear()) : dataTime.getMonthOfYear())
//				+ "-"
//				+ (dataTime.getDayOfMonth() < 10 ? ("0" + dataTime
//						.getDayOfMonth()) : dataTime.getDayOfMonth());
//		String new_indexName = index_name + "-" + index_id + "-" + dateFormat;
 		
 		
 		Map<String,String> tableSimple = new HashMap<String,String>();
 		Map<String,String> tableComplex = new HashMap<String,String>();
 		
 		tableSimple.put("organ_name","tb_organ");
 		tableSimple.put("author_name_cn","tb_author_cn");
 		tableSimple.put("author_name_en","tb_author_cn");
 		tableSimple.put("keywords_name","tb_keywords");
 		
 		tableComplex.put("ref_cn","tb_ref_cn");
 		tableComplex.put("ref_en","tb_ref_en");
 		
 		
 		DBConfigure dbC = new DBConfigure ();
 		dbC.setDatabaseName(databaseName);
 		dbC.setHost(host);
 		dbC.setPassword(password);
 		dbC.setUserName(userName);
 		dbC.setMaxRecoder(maxRecoder);
 		dbC.setIndexType(indexType);
 		dbC.setIndexId(indexId);
 		dbC.setIndexName(indexName);
 		dbC.setIndexTime(indexTime);
 		//ESContent
 		dbC.setPriTableName(priTableName);
 		dbC.setTableComplexNames(tableComplex);
 		dbC.setTableSimpleNames(tableSimple);
 		dbC.setUniqFieldName(uniqFieldName);
 		System.out.println(aa.bulkInsert(dbC));
 	    }
    
    private static void ESSearch( AdminAccount aa) {
    	List<String> clusterList = new ArrayList<String>();
    	//一个集群
        clusterList.add("10.107.6.82:9300"); 
       
        String[] type = new String[] {"paper"};   
        
        String[] indexNames = new String[] { 
        		"cnki-1-2015-05-23"
//        		 "beiyou-1-2015-05-13"
//        		,"beiyou-1-2015-05-14" 
        		};

         
         
         int from = 0;
         int offset = 10;
         String sortField = null;
         String sortType = null;
         
         
         
     	String searchField1 = "keywords_name";    
     	
         SearchType searchType1 = SearchType.range;
         SearchLogic searchLogic1 = SearchLogic.must;
         String queryStringPrecision1 = "100";
         DataFilter dataFilter1 = DataFilter.exists;
         float boost1 = 1.0f;
         boolean highlight1 = false;  
         
         MySearchOption ms1 = new MySearchOption(searchType1,searchLogic1
         		,queryStringPrecision1,dataFilter1,boost1,highlight1);
        
         Object[] searchValue1 = new Object[] {2011,2015,ms1};
     	
         String searchField2= "keywords_name";       
         
         SearchType searchType2 = SearchType.querystring;
         SearchLogic searchLogic2 = SearchLogic.must;
         String queryStringPrecision2 = "100";
         DataFilter dataFilter2 = DataFilter.exists;
         float boost2 = 1.0f;
         boolean highlight2 = false;  
         
         MySearchOption ms2 = new MySearchOption(searchType2,searchLogic2
         		,queryStringPrecision2,dataFilter2,boost2,highlight2);
         Object[] searchValue2 = new Object[]{"北京邮电大学","倪尧天",ms2};       
         
         
         String searchField3 = "abstracts";
         
         SearchType searchType3 = SearchType.querystring;
         SearchLogic searchLogic3 = SearchLogic.should;
         String queryStringPrecision3 = "100";
         DataFilter dataFilter3 = DataFilter.exists;
         float boost3 = 1.0f;
         boolean highlight3 = false;  
         
         MySearchOption ms3 = new MySearchOption(searchType3,searchLogic3
         		,queryStringPrecision3,dataFilter3,boost3,highlight3);
         Object[] searchValue3 = new  Object[]{"早产儿","农村",ms3};
         
         

         
         HashMap<String, Object[]> searchContentMap = new HashMap<String,Object[]>();
        SearchLogic searchLogic = SearchLogic.must;
        	 
        
        	
    //   searchContentMap.put(searchField1, searchValue1);
 	//	searchContentMap.put(searchField2, searchValue2);
 		searchContentMap.put(searchField3, searchValue3);
     
        	
 		String filtField1 = "journal_year"; 
 		
 		Object[] filtValue1 = new Object[]{2013};
 		
 		HashMap<String, Object[]> filterContentMap = new HashMap<String,Object[]>();      	
        	SearchLogic filterLogic = SearchLogic.must;
        	filterContentMap.put(filtField1,filtValue1);
         
        	 
             
         
       	List<Map<String, Object>> rs =  aa.search( indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
       	if(rs.isEmpty()){
				System.out.println("没有检索结果");
		}
       
       
        long count = aa.getCount(indexNames, searchContentMap, searchLogic,filterContentMap,filterLogic);
		System.out.println(count);	
	
    }
   
}
