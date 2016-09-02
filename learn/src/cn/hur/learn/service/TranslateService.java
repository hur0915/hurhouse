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
 * 翻译词典百度API接口操作类
 * 
 */  
public class TranslateService {  
	
private static final String UTF8 = "utf-8";
	
	//申请者开发者id，实际使用时请修改成开发者自己的appid
	private static final String appId = "20160617000023488";

	//申请成功后的证书token，实际使用时请修改成开发者自己的token
	private static final String token = "5Nmk8f954Gso3pigp5OH";

	private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

	//随机数，用于生成md5值，开发者使用时请激活下边第四行代码
	private static final Random random = new Random();
	
	public String translate(String q, String from, String to) throws Exception{
		//用于md5加密
		//int salt = random.nextInt(10000);
		//本演示使用指定的随机数为1435660288
		int salt = 1435660288;
		
		// 对appId+源文+随机数+token计算md5值
		StringBuilder md5String = new StringBuilder();
		md5String.append(appId).append(q).append(salt).append(token);
		String md5 = DigestUtils.md5Hex(md5String.toString());

		//使用Post方式，组装参数
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
		nvps.add(new BasicNameValuePair("q", q));  
	    nvps.add(new BasicNameValuePair("from", from));  
	    nvps.add(new BasicNameValuePair("to", to));  
	    nvps.add(new BasicNameValuePair("appid", appId));  
	    nvps.add(new BasicNameValuePair("salt", String.valueOf(salt)));  
	    nvps.add(new BasicNameValuePair("sign", md5));  
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));  

		//创建httpclient链接，并执行
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    CloseableHttpResponse response = httpclient.execute(httpost);
	    
	    //对于返回实体进行解析
		HttpEntity entity = response.getEntity();
		InputStream returnStream = entity.getContent();
		BufferedReader reader = new BufferedReader(
		new InputStreamReader(returnStream, UTF8));
		StringBuilder result = new StringBuilder();
		String str = null;
		while ((str = reader.readLine()) != null) {
			result.append(str).append("\n");
		}
		
		//转化为json对象，注：Json解析的jar包可选其它
		JSONObject resultJson = JSONObject.fromObject(result.toString());
		
		//开发者自行处理错误，本示例失败返回为null
		try {
			String error_code = resultJson.getString("error_code");
			if (error_code != null) {
				System.out.println("出错代码:" + error_code);
				System.out.println("出错信息:" + resultJson.getString("error_msg"));
				return null;
			}
		} catch (Exception e) {}
		
		//获取返回翻译结果
		JSONArray array = (JSONArray) resultJson.get("trans_result");
		JSONObject dst = (JSONObject) array.get(0);
		String text = dst.getString("dst");
		text = URLDecoder.decode(text, UTF8);

		return text;
	}
	
	//实际抛出异常由开发者自己处理
	public static  String translate(String q) throws Exception{
		TranslateService baidu = new TranslateService();	
		StringBuffer result = new StringBuffer();
		try {
			result.append("多种语言翻译如下：").append("\n\n");
			result.append("中文:").append(baidu.translate(q, "auto", "zh")).append("\n");//中文
			result.append("英语:").append(baidu.translate(q, "auto", "en")).append("\n");//英语
			result.append("粤语:").append(baidu.translate(q, "auto", "yue")).append("\n");//粤语
			result.append("文言文:").append(baidu.translate(q, "auto", "wyw")).append("\n");//文言文
			result.append("日语:").append(baidu.translate(q, "auto", "jp")).append("\n");//日语
			//result.append(baidu.translate(q, "auto", "kor")).append("\n");//韩语
			result.append("法语:").append(baidu.translate(q, "auto", "fra")).append("\n");//法语
			result.append("西班牙语:").append(baidu.translate(q, "auto", "spa")).append("\n");//西班牙语
			//result.append(baidu.translate(q, "auto", "th")).append("\n");//泰语
			//result.append(baidu.translate(q, "auto", "ara")).append("\n");//阿拉伯语
			result.append("俄语:").append(baidu.translate(q, "auto", "ru")).append("\n");//俄语
			result.append("葡萄牙语:").append(baidu.translate(q, "auto", "pt")).append("\n");//葡萄牙语
			result.append("德语:").append(baidu.translate(q, "auto", "de")).append("\n");//德语
			result.append("意大利语:").append(baidu.translate(q, "auto", "it")).append("\n");//意大利语
			result.append("希腊语:").append(baidu.translate(q, "auto", "el")).append("\n");//希腊语
			result.append("荷兰语:").append(baidu.translate(q, "auto", "nl")).append("\n");//荷兰语
			//result.append("波兰语:").append(baidu.translate(q, "auto", "pl")).append("\n");//波兰语
			result.append("保加利亚语:").append(baidu.translate(q, "auto", "bul")).append("\n");//保加利亚语
			result.append("爱沙尼亚语:").append(baidu.translate(q, "auto", "est")).append("\n");//爱沙尼亚语
			result.append("丹麦语:").append(baidu.translate(q, "auto", "dan")).append("\n");//丹麦语
			result.append("芬兰语:").append(baidu.translate(q, "auto", "fin")).append("\n");//芬兰语
			result.append("捷克语:").append(baidu.translate(q, "auto", "cs")).append("\n");//捷克语
			//result.append("罗马尼亚语:").append(baidu.translate(q, "auto", "rom")).append("\n");//罗马尼亚语
			result.append("斯洛文尼亚语:").append(baidu.translate(q, "auto", "slo")).append("\n");//斯洛文尼亚语
			result.append("瑞典语:").append(baidu.translate(q, "auto", "swe")).append("\n");//瑞典语
			result.append("匈牙利语:").append(baidu.translate(q, "auto", "hu")).append("\n");//匈牙利语
			result.append("繁体中文:").append(baidu.translate(q, "auto", "cht")).append("\n");//繁体中文
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	/**
	 * 通过main在本地测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String source = "你好";
		String result = TranslateService.translate(source);
		if(result == null){
			System.out.println("翻译出错，参考百度错误代码和说明。");
			return;
		}
		System.out.println(result);
		System.out.println(result.getBytes().length);  
	}
}  
