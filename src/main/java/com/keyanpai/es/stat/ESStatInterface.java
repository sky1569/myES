package com.keyanpai.es.stat;

public interface ESStatInterface {
	double getMin(String fieldName);
	double getMax(String fieldName);
	double getAvg(String fieldName);
	double getSum(String fieldName);
	long count(String fieldName);
}
