package com.keyanpai.es;

public class MyStatsOption {
	private StatsType statsType = StatsType.avg;
	private StatsInterval statInterval = StatsInterval.year;
	public enum StatsType{
		avg,
		sum,
		count
	}
	public enum StatsInterval{
		year		
	}
	
	public MyStatsOption(){
		
	}
	public MyStatsOption(StatsType statsType,StatsInterval statsInterval){
		this.setStatsInterval(statsInterval);
		this.setStatsType(statsType);
		
	}
	private void setStatsType(StatsType statsType) {
		// TODO Auto-generated method stub
		this.statsType = statsType;
		
	}
	
	private void setStatsInterval(StatsInterval statsInterval) {
		// TODO Auto-generated method stub
		this.statInterval = statsInterval;
	}
	public StatsType getStatsType()
	{
		return this.statsType;
	}
	public StatsInterval getStatsInterval()
	{
		return this.statInterval;
	}
}
