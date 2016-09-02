package cn.hur.learn.pojo;

/**
 * 某一线路
 */
public class Segment {
	
	private String start_stat;//起点站名
	private String end_stat;//终点站名
	private String line_name;//线路名称
	private String stats;//沿途站点
	public String getStart_stat() {
		return start_stat;
	}
	public void setStart_stat(String start_stat) {
		this.start_stat = start_stat;
	}
	public String getEnd_stat() {
		return end_stat;
	}
	public void setEnd_stat(String end_stat) {
		this.end_stat = end_stat;
	}
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public String getStats() {
		return stats;
	}
	public void setStats(String stats) {
		this.stats = stats;
	}
}
