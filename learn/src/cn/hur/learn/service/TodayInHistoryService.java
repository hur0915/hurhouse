package cn.hur.learn.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import cn.hur.learn.util.CommonUtil;

/**
 * 历史上的今天官网API接口操作类
 * 
 */
public class TodayInHistoryService {

	/**
	 * 从html中抽取出历史上的今天信息
	 * 
	 * @param html
	 * @return
	 */
	private static String extract(String html) {
		StringBuffer buffer = null;
		StringBuffer newBuffer = null;
		// 日期标签：区分是昨天还是今天
		String dateTag = getMonthDay(0);

		Pattern p = Pattern.compile("(.*)(<ul class=\"list clearfix\">)(.*?)(</ul>)(.*)");
		Matcher m = p.matcher(html);
		if (m.matches()) {
			buffer = new StringBuffer();
			if (m.group(3).contains(getMonthDay(-1)))
				dateTag = getMonthDay(-1);

			// 拼装标题
//			buffer.append("≡≡").append("历史上的").append(dateTag).append("≡≡").append("\n");

			// 抽取需要的数据
			for (String info : m.group(3).split("  ")) {
				info = info.replace(dateTag, "").replaceAll("</?[^>]+>", "").trim();
				buffer.append(info);
			}
			String s = buffer.toString();
			
			newBuffer = new StringBuffer();
			newBuffer.append("≡≡").append("历史上的").append(dateTag).append("≡≡").append("\n");
	        String[] str = s.split("\\s+");
	        for (int j=str.length-1;j>0;j=j-2) {
	        	newBuffer.append(str[j-1]).append(str[j]).append("\n");
	        }
	        newBuffer.substring(0,newBuffer.length()-2);
		}
		return (null == newBuffer) ? null : newBuffer.toString();
		
	}

	/**
	 * 获取前/后n天日期(M月d日)
	 * 
	 * @return
	 */
	private static String getMonthDay(int diff) {
		DateFormat df = new SimpleDateFormat("0M月d日");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, diff);
		return df.format(c.getTime());
	}

	/**
	 * 封装历史上的今天查询方法，供外部调用
	 * 
	 * @return
	 */
	public static String getTodayInHistoryInfo() {
		// 获取网页源代码
		String html = CommonUtil.httpRequest("http://www.lssdjt.com/", "GET", null);
		// 从网页中抽取信息
		String result = extract(html);

		return result;
	}

	/**
	 * 通过main在本地测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String info = getTodayInHistoryInfo();
		System.out.println(info);
	}
}

