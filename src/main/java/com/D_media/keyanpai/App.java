package com.D_media.keyanpai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.keyanpai.dbImp.dbServiceImp;
import com.keyanpai.esImp.ESControlImp;
import com.keyanpai.esImp.ESSearchImp;
import com.keyanpai.escontent.cnkiContent;
import com.keyanpai.instance.DBConfigure;
import com.keyanpai.instance.ESClient;
import com.keyanpai.instance.MySearchOption;
import com.keyanpai.instance.MySearchOption.DataFilter;
import com.keyanpai.instance.MySearchOption.SearchLogic;
import com.keyanpai.instance.MySearchOption.SearchType;




/**
 * Hello world!
 *
 */
public class App 
{
	
    public static void main( String[] args ) 
    {
        System.out.println( "Hello World!!!!!!!!!!!!!!!!!" );
        
       	List<String> clusterList = new ArrayList<String>();
    	//一个集群
        clusterList.add("10.107.6.82:9300"); 
        ESClient esClient = new ESClient(clusterList);
        DBInsert(esClient);
        
//        cnkiContent cc = new cnkiContent();
//        cc.setDatabaseName("cnki_2015_0419");
//        cc.setPriTableName("tb_cnki");
//        cc.setUniqFieldName("uniq_id");
        
    	
    }
    
    private static void ESDelete( ESClient esClient){
    	
    	 ESControlImp esControlImp = ESControlImp.getInstance();
    	 esClient.clientConn();
    	 esControlImp.controlConfigure(esClient.getESClient());
    	 String[] indexName = new String[]{"cnki-1-2015-05-23"};
    	 HashMap<String,Object[]> contentMap = new HashMap<String,Object[]>();
    	 
    	 esControlImp.bulkDelete(indexName, contentMap);
    	 
    }
    private static void DBInsert( ESClient esClient){
    	 ESControlImp esControlImp = ESControlImp.getInstance();
    	 esClient.clientConn();
    	 esControlImp.controlConfigure(esClient.getESClient());
    	 
    	 
    	 
    	//DBConfigure dbConfigure = new DBConfigure();
    	String host = "10.105.223.24";
 		String userName = "root";
 		String password = "buptmitc"; 		
 		int maxRecoder = 4000;
 		
 		String databaseName = "cnki_2015_0419";
 		String priTableName = "tb_cnki";
 		String uniqFieldName = "uniq_id"; 		
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
 		
 		//ESContent
 		dbC.setPriTableName(priTableName);
 		dbC.setTableComplexNames(tableComplex);
 		dbC.setTableSimpleNames(tableSimple);
 		dbC.setUniqFieldName(uniqFieldName);
 		
 		
 		dbServiceImp ds = dbServiceImp.getDBServiceImp();
    	ds.DBSetter(dbC);
    	ds.open();
    	ds.handlData(esControlImp);
    	ds.close();
    	esClient.destroy(); 
    	
    }
    
    private static void ESSearch( ESClient esClient) {
//    	List<String> clusterList = new ArrayList<String>();
//    	//一个集群
//        clusterList.add("10.107.6.82:9300");   
//      ESClient esClient = new ESClient(clusterList);
        String[] type = new String[] {"paper"};   
        
        String[] indexNames = new String[] { 
        	//	"cnki-1-2015-05-20"
        		 "beiyou-1-2015-05-13"
        		,"beiyou-1-2015-05-14" 
        		};

         ESSearchImp esSearchImp = new ESSearchImp();
         esClient.clientConn();
         esSearchImp.searchConfigure(esClient.getESClient());
         
         
         int from = 0;
         int offset = 10;
         String sortField = null;
         String sortType = null;
         
         
         
     	String searchField1 = "journal_year";    
     	
         SearchType searchType1 = SearchType.range;
         SearchLogic searchLogic1 = SearchLogic.must;
         String queryStringPrecision1 = "100";
         DataFilter dataFilter1 = DataFilter.exists;
         float boost1 = 1.0f;
         boolean highlight1 = false;  
         
         MySearchOption ms1 = new MySearchOption(searchType1,searchLogic1
         		,queryStringPrecision1,dataFilter1,boost1,highlight1);
        
         Object[] searchValue1 = new Object[] {2011,2015,ms1};
     	
         String searchField2= "name";       
         
         SearchType searchType2 = SearchType.querystring;
         SearchLogic searchLogic2 = SearchLogic.must;
         String queryStringPrecision2 = "100";
         DataFilter dataFilter2 = DataFilter.exists;
         float boost2 = 1.0f;
         boolean highlight2 = false;  
         
         MySearchOption ms2 = new MySearchOption(searchType2,searchLogic2
         		,queryStringPrecision2,dataFilter2,boost2,highlight2);
         Object[] searchValue2 = new Object[]{"中国","农村",ms2};       
         
         
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
        	 
        
        	
         searchContentMap.put(searchField1, searchValue1);
 		searchContentMap.put(searchField2, searchValue2);
 	//	searchContentMap.put(searchField3, searchValue3);
     
        	
 		String filtField1 = "journal_year"; 
 		
 		Object[] filtValue1 = new Object[]{2013};
 		
 		HashMap<String, Object[]> filterContentMap = new HashMap<String,Object[]>();      	
        	SearchLogic filterLogic = SearchLogic.must;
        	filterContentMap.put(filtField1,filtValue1);
         
         
         
       	List<Map<String, Object>> rs =  esSearchImp.simpleSearch(indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic, from, offset, sortField, sortType);
       	if(rs.isEmpty()){
				System.out.println("没有检索结果");
		}
       
       
        long count = esSearchImp.getCount(indexNames, searchContentMap, searchLogic,filterContentMap,filterLogic);
		System.out.println(count);	
		esClient.destroy();
    }
    /*
    private static DBServiceImp DBconfigure() {
		// TODO Auto-generated method stub
    	// 建立链接
    	String host = "10.105.223.24";
		String userName = "root";
		String password = "buptmitc";
		String databaseName = "cnki_2015_0419";
		String uniqFieldName = "uniq_id";
		String priTableName = "tb_cnki";
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
		dbC.setPriTableName(priTableName);
		dbC.setTableComplexNames(tableComplex);
		dbC.setTableSimpleNames(tableSimple);
		dbC.setUniqFieldName(uniqFieldName);
		dbC.setUserName(userName);
		
   	 	DBServiceImp ds =  new DBServiceImp(dbC);
    	

    	 return ds;
	}



	public static ESServiceImp ESconfigure()
    {
		ESServiceImp es = new ESServiceImp();
    	List<String> clusterList = new ArrayList<String>();
    	//一个集群
        clusterList.add("10.107.6.82:9300");        
    	 // clusterList.add("127.0.0.1:9300");
    	//	HashMap<String, String> ESClientConfigureMap = null;
        try {
        	es.setClusterList(clusterList);       
		//	es.setESClientConfigureMap(ESClientConfigureMap);
			es.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return es;
    }
    public static void search(ESServiceImp es)
    {
        String type = "paper";   
        
        String[] indexNames = new String[] { 
        	//	"cnki-1-2015-05-20"
        		 "beiyou-1-2015-05-13"
        		,"beiyou-1-2015-05-14" 
        		};
    	 
        int from = 0;
        int offset = 10;
        String sortField = null;
        String sortType = null;
        
        
        
    	String searchField1 = "journal_year";    
    	
        SearchType searchType1 = SearchType.range;
        SearchLogic searchLogic1 = SearchLogic.must;
        String queryStringPrecision1 = "100";
        DataFilter dataFilter1 = DataFilter.exists;
        float boost1 = 1.0f;
        boolean highlight1 = false;  
        
        MySearchOption ms1 = new MySearchOption(searchType1,searchLogic1
        		,queryStringPrecision1,dataFilter1,boost1,highlight1);
       
        Object[] searchValue1 = new Object[] {2011,2015,ms1};
    	
        String searchField2= "name";       
        
        SearchType searchType2 = SearchType.querystring;
        SearchLogic searchLogic2 = SearchLogic.must;
        String queryStringPrecision2 = "100";
        DataFilter dataFilter2 = DataFilter.exists;
        float boost2 = 1.0f;
        boolean highlight2 = false;  
        
        MySearchOption ms2 = new MySearchOption(searchType2,searchLogic2
        		,queryStringPrecision2,dataFilter2,boost2,highlight2);
        Object[] searchValue2 = new Object[]{"中国","农村",ms2};       
        
        
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
       	 
       
       	
        searchContentMap.put(searchField1, searchValue1);
		searchContentMap.put(searchField2, searchValue2);
	//	searchContentMap.put(searchField3, searchValue3);
    
       	
		String filtField1 = "journal_year"; 
		
		Object[] filtValue1 = new Object[]{2013};
		
		HashMap<String, Object[]> filterContentMap = new HashMap<String,Object[]>();      	
       	SearchLogic filterLogic = SearchLogic.must;
       	filterContentMap.put(filtField1,filtValue1);
       	
       	
       	
       	
		List<Map<String, Object>> rs = es.simpleSearch(indexNames, searchContentMap, searchLogic, filterContentMap, filterLogic,from, offset, sortField, sortType);
        	
		if(rs.isEmpty()){
				System.out.println("没有检索结果");
			}
//		else{
//			for(Map<String,Object> mrs : rs)
//			{
//				for (Entry<String, Object> mrse : mrs.entrySet()) {
//					String fieldTmp = mrse.getKey();
//					Object valueTmp = mrse.getValue();
//					System.out.println(fieldTmp + ":" + valueTmp);
//				}
//			}			
//		}		
		
		long count = es.getCount(indexNames, searchContentMap, searchLogic,filterContentMap,filterLogic);
		
		System.out.println(count);	
		
	
    }
    public static void insert(  ESServiceImp es, DBServiceImp ds )
    {
    	try {
			ds.handlerData(es);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
    
    	
    }*/
}
