package cn.hur.learn.service;

import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cn.hur.learn.pojo.Bus;
import cn.hur.learn.pojo.Root;
import cn.hur.learn.util.CommonUtil;


/**
 * ��������API�ӿڲ�����
 *
 */
public class BusService {
	
	public static String getBusInfo(String source){
    	String url = "http://openapi.aibang.com/bus/transfer?app_key=APP_KEY&city=CITY&start_addr=START_ADDR&end_addr=END_ADDR";
    	String [] strs = source.split("[��]");
        String city = strs[0];
        String start_addr = strs[1];
        String end_addr = strs[2];
    	url = url.replace("APP_KEY", "f41c8afccc586de03a99c86097e98ccb");
    	url = url.replace("CITY", CommonUtil.urlEncode(city,"utf-8"));
    	url = url.replace("START_ADDR", CommonUtil.urlEncode(start_addr,"utf-8"));
    	url = url.replace("END_ADDR", CommonUtil.urlEncode(end_addr,"utf-8"));
    	System.out.println(url);
        String xml = CommonUtil.httpRequest(url, "GET", null);
        XStream xstream = new XStream();
        Root root = (Root) xstream.fromXML(xml);
        System.out.println(root.toString());
        List<Bus> buses = root.getBuses();
        
        StringBuffer buffer = new StringBuffer();  
        buffer.append("���� ").append(buses.size()).append(" �ֳ˳�·��").append("\n");  
        for (Bus bus : buses) {  
        	buffer.append("·��һ").append("\n");
        	buffer.append("Ԥ�ƻ���ʱ�䣺").append(bus.getTime()).append("\n");
            buffer.append("��").append(bus.getSegments().getLine_name()).append(","); 
            buffer.append("��").append(bus.getSegments().getStart_stat()).append("����վ�ϳ�").append(",");
            buffer.append(";��վ�㣺").append(bus.getSegments().getStats().replace(";", "����>")).append(","); 
            buffer.append("�����").append(bus.getLast_foot_dist()).append("�׵���").append(bus.getSegments().getEnd_stat()).append("\n\n"); 
        }  
		// �Ƴ�ĩβ�ո�  
    	buffer = new StringBuffer(buffer.substring(0, buffer.lastIndexOf("\n")));  
    	return buffer.toString();  
	}
	
	
    /** 
     * ��������
     * @param args 
     */  
    public static void main(String[] args) {  
    	String URL = getBusInfo("�ϲ�������·����վ");
    	System.out.println(URL);
    }
}
