package cn.hur.learn.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


/** 
 * ����ʵ�ٶ�API�ӿڲ�����
 * 
 */  
public class TranslateService {  
	
private static final String UTF8 = "utf-8";
	
	//�����߿�����id��ʵ��ʹ��ʱ���޸ĳɿ������Լ���appid
	private static final String appId = "20160617000023488";

	//����ɹ����֤��token��ʵ��ʹ��ʱ���޸ĳɿ������Լ���token
	private static final String token = "5Nmk8f954Gso3pigp5OH";

	private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

	//���������������md5ֵ��������ʹ��ʱ�뼤���±ߵ����д���
	private static final Random random = new Random();
	
	public String translate(String q, String from, String to) throws Exception{
		//����md5����
		//int salt = random.nextInt(10000);
		//����ʾʹ��ָ���������Ϊ1435660288
		int salt = 1435660288;
		
		// ��appId+Դ��+�����+token����md5ֵ
		StringBuilder md5String = new StringBuilder();
		md5String.append(appId).append(q).append(salt).append(token);
		String md5 = DigestUtils.md5Hex(md5String.toString());

		//ʹ��Post��ʽ����װ����
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		nvps.add(new BasicNameValuePair("q", q));  
	    nvps.add(new BasicNameValuePair("from", from));  
	    nvps.add(new BasicNameValuePair("to", to));  
	    nvps.add(new BasicNameValuePair("appid", appId));  
	    nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
	    nvps.add(new BasicNameValuePair("sign", md5));  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));  

		//����httpclient���ӣ���ִ��
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httpost);
	    
	    //���ڷ���ʵ����н���
		HttpEntity entity = response.getEntity();
		InputStream returnStream = entity.getContent();
		BufferedReader reader = new BufferedReader(
		new InputStreamReader(returnStream, UTF8));
		StringBuilder result = new StringBuilder();
		String str = null;
		while ((str = reader.readLine()) != null) {
			result.append(str).append("\n");
		}
		
		//ת��Ϊjson����ע��Json������jar����ѡ����
		JSONObject resultJson = JSONObject.fromObject(result.toString());
		
		//���������д�����󣬱�ʾ��ʧ�ܷ���Ϊnull
		try {
			String error_code = resultJson.getString("error_code");
			if (error_code != null) {
				System.out.println("�������:" + error_code);
				System.out.println("������Ϣ:" + resultJson.getString("error_msg"));
				return null;
			}
		} catch (Exception e) {}
		
		//��ȡ���ط�����
		JSONArray array = (JSONArray) resultJson.get("trans_result");
		JSONObject dst = (JSONObject) array.get(0);
		String text = dst.getString("dst");
		text = URLDecoder.decode(text, UTF8);

		return text;
	}
	
	//ʵ���׳��쳣�ɿ������Լ�����
	public static  String translate(String q) throws Exception{
		TranslateService baidu = new TranslateService();	
		StringBuffer result = new StringBuffer();
		try {
			result.append("�������Է������£�").append("\n\n");
			result.append("����:").append(baidu.translate(q, "auto", "zh")).append("\n");//����
			result.append("Ӣ��:").append(baidu.translate(q, "auto", "en")).append("\n");//Ӣ��
			result.append("����:").append(baidu.translate(q, "auto", "yue")).append("\n");//����
			result.append("������:").append(baidu.translate(q, "auto", "wyw")).append("\n");//������
			result.append("����:").append(baidu.translate(q, "auto", "jp")).append("\n");//����
			//result.append(baidu.translate(q, "auto", "kor")).append("\n");//����
			result.append("����:").append(baidu.translate(q, "auto", "fra")).append("\n");//����
			result.append("��������:").append(baidu.translate(q, "auto", "spa")).append("\n");//��������
			//result.append(baidu.translate(q, "auto", "th")).append("\n");//̩��
			//result.append(baidu.translate(q, "auto", "ara")).append("\n");//��������
			result.append("����:").append(baidu.translate(q, "auto", "ru")).append("\n");//����
			result.append("��������:").append(baidu.translate(q, "auto", "pt")).append("\n");//��������
			result.append("����:").append(baidu.translate(q, "auto", "de")).append("\n");//����
			result.append("�������:").append(baidu.translate(q, "auto", "it")).append("\n");//�������
			result.append("ϣ����:").append(baidu.translate(q, "auto", "el")).append("\n");//ϣ����
			result.append("������:").append(baidu.translate(q, "auto", "nl")).append("\n");//������
			//result.append("������:").append(baidu.translate(q, "auto", "pl")).append("\n");//������
			result.append("����������:").append(baidu.translate(q, "auto", "bul")).append("\n");//����������
			result.append("��ɳ������:").append(baidu.translate(q, "auto", "est")).append("\n");//��ɳ������
			result.append("������:").append(baidu.translate(q, "auto", "dan")).append("\n");//������
			result.append("������:").append(baidu.translate(q, "auto", "fin")).append("\n");//������
			result.append("�ݿ���:").append(baidu.translate(q, "auto", "cs")).append("\n");//�ݿ���
			//result.append("����������:").append(baidu.translate(q, "auto", "rom")).append("\n");//����������
			result.append("˹����������:").append(baidu.translate(q, "auto", "slo")).append("\n");//˹����������
			result.append("�����:").append(baidu.translate(q, "auto", "swe")).append("\n");//�����
			result.append("��������:").append(baidu.translate(q, "auto", "hu")).append("\n");//��������
			result.append("��������:").append(baidu.translate(q, "auto", "cht")).append("\n");//��������
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	/**
	 * ͨ��main�ڱ��ز���
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String source = "���";
		String result = TranslateService.translate(source);
		if(result == null){
			System.out.println("��������ο��ٶȴ�������˵����");
			return;
		}
		System.out.println(result);
		System.out.println(result.getBytes().length);  
	}
}  
