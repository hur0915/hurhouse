package cn.hur.learn.pojo;

/**
 * ������·
 */
public class Bus {
	
	private int time;//���ƺķ�ʱ��
	private int foot_dist;//�ܲ��о���
	private int last_foot_dist;//���յ�վ�ߵ��յ�ľ���
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
