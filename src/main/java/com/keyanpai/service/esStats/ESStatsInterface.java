package com.keyanpai.service.esStats;

public interface ESStatsInterface {
	double getMin(String fieldName);
	double getMax(String fieldName);
	double getAvg(String fieldName);
	double getSum(String fieldName);
	long count(String fieldName);
}
