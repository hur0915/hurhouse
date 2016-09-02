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
 * ��Ϣ��������
 * 
 */
public class MessageUtil {
	// ������Ϣ���ͣ��ı�
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";
	// ������Ϣ���ͣ�ͼƬ
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
	// ������Ϣ���ͣ�����
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
	// ������Ϣ���ͣ���Ƶ
	public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
	// ������Ϣ���ͣ�С��Ƶ
	public static final String REQ_MESSAGE_TYPE_SHORTVIDEO = "shortvideo";
	// ������Ϣ���ͣ�����λ��
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
	// ������Ϣ���ͣ�����
	public static final String REQ_MESSAGE_TYPE_LINK = "link";
	
	// ������Ϣ���ͣ��¼�����
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";
	
	// �¼����ͣ�subscribe(����)
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	// �¼����ͣ�unsubscribe(ȡ������)
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	// �¼����ͣ�scan(��ע�û�ɨ���������ά��)
	public static final String EVENT_TYPE_SCAN = "scan";
	// �¼����ͣ�LOCATION(�ϱ�����λ��)
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	// �¼����ͣ�CLICK(�Զ���˵�����¼�)
	public static final String EVENT_TYPE_CLICK = "CLICK";
	
	// ��Ӧ��Ϣ���ͣ��ı�
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";
	// ��Ӧ��Ϣ���ͣ�ͼƬ
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";
	// ��Ӧ��Ϣ���ͣ�����
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice";
	// ��Ӧ��Ϣ���ͣ���Ƶ
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";
	// ��Ӧ��Ϣ���ͣ�����
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
	// ��Ӧ��Ϣ���ͣ�ͼ��
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * ��ȡ΢�żӽ��ܵ�ʵ��
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
	 * ����΢�ŷ���������XML��
	 * ����ģʽ�½����������
	 * @param request
	 * @return Map<String, String>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		// ����������洢��HashMap��
		Map<String, String> map = new HashMap<String, String>();

		// ��request��ȡ��������
		InputStream inputStream = request.getInputStream();
		// ��ȡ������
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// �õ�xml��Ԫ��
		Element root = document.getRootElement();
		
		recursiveParseXml(root,map);
		return map;
	}
	
	/**
	 * ����΢�ŷ���������XML��
	 * ����ģʽ�½����������
	 * @param request
	 * @return Map<String, String>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXmlCrypt(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
		/**
		 * ��1������InputStream��ȡXML�ı�
		 */
		InputStream is = request.getInputStream();
		BufferedReader br= new BufferedReader(new InputStreamReader(is));
		
		//ÿ�ζ�ȡ������
		String line = null;
		//���ն�ȡ������
		StringBuffer buffer = new StringBuffer();
		while((line = br.readLine()) != null){
			buffer.append(line);
		}
		br.close();
		is.close();
		
		/**
		 * ��2��������
		 */
		String msgSignature = request.getParameter("msg_signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		WXBizMsgCrypt wxCrypt = MessageUtil.getWXBizMsgCrypt();
		String fromXML = wxCrypt.decryptMsg(msgSignature, timestamp, nonce, buffer.toString());
		
		/**
		 * ��3��������XML����ȡ�������
		 */
		Document doc =DocumentHelper.parseText(fromXML);
		// �õ�xml��Ԫ��
		Element root = doc.getRootElement();
		// �õ���Ԫ�ص������ӽڵ�
		List<Element> elementList = root.elements();

		recursiveParseXml(root,map);
		return map;
	}
	
	//�ݹ����XML
	private static void recursiveParseXml(Element root,Map<String, String> map){
		//��ȡ���ڵ�������ӽڵ�
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
	 * ��չxstream��ʹ��֧��CDATA
	 * 
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// ������xml�ڵ��ת��������CDATA���
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
	 * �ı���Ϣ����ת����xml
	 * 
	 * @param textMessage �ı���Ϣ����
	 * @return xml
	 */
	public static String messageToXml(TextMessage textMessage) {
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}
	
	/**
	 * ͼƬ��Ϣ����ת����xml
	 * 
	 * @param imageMessage ͼƬ��Ϣ����
	 * @return xml
	 */
	public static String messageToXml(ImageMessage imageMessage) {
		xstream.alias("xml", imageMessage.getClass());
		return xstream.toXML(imageMessage);
	}
	
	/**
	 * ������Ϣ����ת����xml
	 * 
	 * @param voiceMessage ������Ϣ����
	 * @return xml
	 */
	public static String messageToXml(VoiceMessage voiceMessage) {
		xstream.alias("xml", voiceMessage.getClass());
		return xstream.toXML(voiceMessage);
	}
	
	/**
	 * ��Ƶ��Ϣ����ת����xml
	 * 
	 * @param videoMessage ��Ƶ��Ϣ����
	 * @return xml
	 */
	public static String messageToXml(VideoMessage videoMessage) {
		xstream.alias("xml", videoMessage.getClass());
		return xstream.toXML(videoMessage);
	}

	/**
	 * ������Ϣ����ת����xml
	 * 
	 * @param musicMessage ������Ϣ����
	 * @return xml
	 */
	public static String messageToXml(MusicMessage musicMessage) {
		xstream.alias("xml", musicMessage.getClass());
		return xstream.toXML(musicMessage);
	}

	/**
	 * ͼ����Ϣ����ת����xml
	 * 
	 * @param newsMessage ͼ����Ϣ����
	 * @return xml
	 */
	public static String messageToXml(NewsMessage newsMessage) {
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new Article().getClass());
		return xstream.toXML(newsMessage);
	}

}


