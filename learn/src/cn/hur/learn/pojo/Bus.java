package cn.hur.learn.pojo;

/**
 * 公交线路
 */
public class Bus {
	
	private int time;//估计耗费时间
	private int foot_dist;//总步行距离
	private int last_foot_dist;//从终点站走到终点的距离
	private Segment segments;
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getFoot_dist() {
		return foot_dist;
	}
	public void setFoot_dist(int foot_dist) {
		this.foot_dist = foot_dist;
	}
	public int getLast_foot_dist() {
		return last_foot_dist;
	}
	public void setLast_foot_dist(int last_foot_dist) {
		this.last_foot_dist = last_foot_dist;
	}
	public Segment getSegments() {
		return segments;
	}
	public void setSegments(Segment segments) {
		this.segments = segments;
	}

}
