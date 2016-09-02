package cn.hur.learn.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.hur.learn.message.resp.Article;
import cn.hur.learn.message.resp.ImageMessage;
import cn.hur.learn.message.resp.MusicMessage;
import cn.hur.learn.message.resp.NewsMessage;
import cn.hur.learn.message.resp.TextMessage;
import cn.hur.learn.message.resp.VideoMessage;
import cn.hur.learn.message.resp.VoiceMessage;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 消息处理工具类
 * 
 */
public class MessageUtil {
	// 请求消息类型：文本
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";
	// 请求消息类型：图片
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
	// 请求消息类型：语音
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
	// 请求消息类型：视频
	public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
	// 请求消息类型：小视频
	public static final String REQ_MESSAGE_TYPE_SHORTVIDEO = "shortvideo";
	// 请求消息类型：地理位置
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
	// 请求消息类型：链接
	public static final String REQ_MESSAGE_TYPE_LINK = "link";
	
	// 请求消息类型：事件推送
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";
	
	// 事件类型：subscribe(订阅)
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	// 事件类型：unsubscribe(取消订阅)
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	// 事件类型：scan(关注用户扫描带参数二维码)
	public static final String EVENT_TYPE_SCAN = "scan";
	// 事件类型：LOCATION(上报地理位置)
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	// 事件类型：CLICK(自定义菜单点击事件)
	public static final String EVENT_TYPE_CLICK = "CLICK";
	
	// 响应消息类型：文本
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";
	// 响应消息类型：图片
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";
	// 响应消息类型：语音
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice";
	// 响应消息类型：视频
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";
	// 响应消息类型：音乐
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
	// 响应消息类型：图文
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 获取微信加解密的实例
	 * @return
	 */
	public static WXBizMsgCrypt getWXBizMsgCrypt(){
		WXBizMsgCrypt pc = null;
		try {
			pc = new WXBizMsgCrypt(SignUtil.token, "087aiBHKDXh7cJ9zp0RkQmeHvdfvIzGvywBTYZYJlSu", "wx3713072305861791");
		} catch (AesException e) {
			e.printStackTrace();
		}
		return pc; 
	}
	
	/**
	 * 解析微信发来的请求（XML）
	 * 明文模式下解析请求参数
	 * @param request
	 * @return Map<String, String>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();

		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到xml根元素
		Element root = document.getRootElement();
		
		recursiveParseXml(root,map);
		return map;
	}
	
	/**
	 * 解析微信发来的请求（XML）
	 * 加密模式下解析请求参数
	 * @param request
	 * @return Map<String, String>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXmlCrypt(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
		/**
		 * 第1步：从InputStream读取XML文本
		 */
		InputStream is = request.getInputStream();
		BufferedReader br= new BufferedReader(new InputStreamReader(is));
		
		//每次读取的内容
		String line = null;
		//最终读取的内容
		StringBuffer buffer = new StringBuffer();
		while((line = br.readLine()) != null){
			buffer.append(line);
		}
		br.close();
		is.close();
		
		/**
		 * 第2步：解密
		 */
		String msgSignature = request.getParameter("msg_signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		WXBizMsgCrypt wxCrypt = MessageUtil.getWXBizMsgCrypt();
		String fromXML = wxCrypt.decryptMsg(msgSignature, timestamp, nonce, buffer.toString());
		
		/**
		 * 第3步：解析XML，获取请求参数
		 */
		Document doc =DocumentHelper.parseText(fromXML);
		// 得到xml根元素
		Element root = doc.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();

		recursiveParseXml(root,map);
		return map;
	}
	
	//递归解析XML
	private static void recursiveParseXml(Element root,Map<String, String> map){
		//获取根节点的所有子节点
		List<Element> elementList = root.elements();
		
		if(elementList.size() == 0){
			map.put(root.getName(), root.getTextTrim());
		}else{
			for(Element e: elementList){
				recursiveParseXml(e,map);
			}
		}		
	}
	
	/**
	 * 扩展xstream，使其支持CDATA
	 * 
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有xml节点的转换都增加CDATA标记
				boolean cdata = true;

				@SuppressWarnings("unchecked")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});

	/**
	 * 文本消息对象转换成xml
	 * 
	 * @param textMessage 文本消息对象
	 * @return xml
	 */
	public static String messageToXml(TextMessage textMessage) {
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}
	
	/**
	 * 图片消息对象转换成xml
	 * 
	 * @param imageMessage 图片消息对象
	 * @return xml
	 */
	public static String messageToXml(ImageMessage imageMessage) {
		xstream.alias("xml", imageMessage.getClass());
		return xstream.toXML(imageMessage);
	}
	
	/**
	 * 语音消息对象转换成xml
	 * 
	 * @param voiceMessage 语音消息对象
	 * @return xml
	 */
	public static String messageToXml(VoiceMessage voiceMessage) {
		xstream.alias("xml", voiceMessage.getClass());
		return xstream.toXML(voiceMessage);
	}
	
	/**
	 * 视频消息对象转换成xml
	 * 
	 * @param videoMessage 视频消息对象
	 * @return xml
	 */
	public static String messageToXml(VideoMessage videoMessage) {
		xstream.alias("xml", videoMessage.getClass());
		return xstream.toXML(videoMessage);
	}

	/**
	 * 音乐消息对象转换成xml
	 * 
	 * @param musicMessage 音乐消息对象
	 * @return xml
	 */
	public static String messageToXml(MusicMessage musicMessage) {
		xstream.alias("xml", musicMessage.getClass());
		return xstream.toXML(musicMessage);
	}

	/**
	 * 图文消息对象转换成xml
	 * 
	 * @param newsMessage 图文消息对象
	 * @return xml
	 */
	public static String messageToXml(NewsMessage newsMessage) {
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new Article().getClass());
		return xstream.toXML(newsMessage);
	}

}


