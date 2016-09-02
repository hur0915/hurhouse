package cn.hur.learn.service;


import cn.hur.learn.util.CommonUtil;


/**
 * ���100API�ӿڲ�����
 *
 */
public class DeliveryService {
	
    
    public static String getDeliveryInfo(String source){
    	String url = "http://m.kuaidi100.com/index_all.html?type=TYPE&postid=POSTID";
    	String [] strs = source.split("[��]");
        String type = strs[0];
        String postid = strs[1];
    	url = url.replace("TYPE", CommonUtil.urlEncode(type,"utf-8"));
    	url = url.replace("POSTID", CommonUtil.urlEncode(postid,"utf-8"));
    	return url;
    }
    /** 
     * ��������
     * @param args 
     */  
    public static void main(String[] args) {  
        String URL = getDeliveryInfo("Բͨ��882264172326237540");
        System.out.println(URL);
    }
}
