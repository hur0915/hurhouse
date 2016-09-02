package cn.hur.learn.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hur.learn.message.resp.Article;
import cn.hur.learn.message.resp.NewsMessage;
import cn.hur.learn.message.resp.TextMessage;
import cn.hur.learn.pojo.BaiduPlace;
import cn.hur.learn.pojo.News;
import cn.hur.learn.pojo.UserLocation;
import cn.hur.learn.util.BaiduMapUtil;
import cn.hur.learn.util.MessageUtil;
import cn.hur.learn.util.MySQLUtil;

/**
 * ����ҵ����
 * 
 */
public class CoreService {
	/**
	 * ����΢�ŷ���������
	 * 
	 * @param request
	 * @return
	 */
	public static String process(Map<String, String> requestMap) {
		// XML��ʽ����Ϣ����
		String respXML = null;
		// Ĭ�Ϸ��ص��ı���Ϣ����
		String respContent =null;
		try {
			// ���ͷ��˺�
			String fromUserName = requestMap.get("FromUserName");
			// ������΢�ź�
			String toUserName = requestMap.get("ToUserName");
			// ��Ϣ����
			String msgType = requestMap.get("MsgType");
			// ��Ϣ����ʱ��
			String createTime = requestMap.get("CreateTime");

			// �ظ��ı���Ϣ
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

			// �ı���Ϣ
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				// ��ȡ�û��������ı���Ϣ
				String content=requestMap.get("Content").trim();
				// ���û����ı���Ϣ����ƥ��
	
				// ��ͷΪ���룬����з���ʵ�
				if(content.startsWith("����")){
					String keyWord = content.replaceAll("^����", "").trim();
					if ("".equals(keyWord)) {
						respContent=getTranslateUsage();
					} 
					else {
						respContent=TranslateService.translate(keyWord);
					}
				}
				
				// ��ͷΪ�����������������ѯ
				else if(content.startsWith("����") || content.endsWith("����")){
					String keyWord = content.replaceAll("^����", "").trim();
					if ("".equals(keyWord)) {
						respContent=getWeatherUsage();
					} 
					else {
						respContent=WeatherService.getWeatherInfo(keyWord);
					}
				}
				
				// ��ͷΪ��ݣ�����п�ݲ�ѯ
				else if(content.startsWith("���")){
					String keyWord = content.replaceAll("^���", "").trim();
					if ("".equals(keyWord)) {
						respContent=getDeliveryUsage();
					} 
					else {
						respContent=DeliveryService.getDeliveryInfo(keyWord);
					}
				}
				
				// ��ͷΪ���ţ���������Ų�ѯ
				else if(content.startsWith("����") || content.endsWith("����")){
					String keyWord = content.replaceAll("^����", "").trim();
					if ("".equals(keyWord)) {
						respContent=getNewsUsage();
					} 
					else {
						List<News> newsList=NewsService.getNewsInfo(keyWord);
						if(null == newsList || 0 == newsList.size()){
							respContent = "�Բ���δ��ѯ��������ţ�";
						}else{
							List<Article> articleList = NewsService.makeArticleList(newsList);
							// �ظ�ͼ����Ϣ
							NewsMessage newsMessage = new NewsMessage();
							newsMessage.setToUserName(fromUserName);
							newsMessage.setFromUserName(toUserName);
							newsMessage.setCreateTime(new Date().getTime());
							newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
							newsMessage.setArticles(articleList);
							newsMessage.setArticleCount(5);
							respXML = MessageUtil.messageToXml(newsMessage);
						}
					}
					return respXML;
				}
				
				// ������ʷ�ϵĽ��죬���в���
				else if(content.startsWith("��ʷ�ϵĽ���")){
					respContent=TodayInHistoryService.getTodayInHistoryInfo();
				}
				
				
				// ��ͷΪ����,������ܱ�����
				else if (content.startsWith("����")) {
					String keyWord = content.replaceAll("����", "").trim();
					// ��ȡ�û����һ�η��͵ĵ���λ��
					UserLocation location = MySQLUtil.getLastLocation(fromUserName);
					// δ��ȡ��
					if (null == location) {
						respContent = getSearchUsage();
					} else {
						// ����ת���󣨾�ƫ�������������ܱ�POI
						List<BaiduPlace> placeList = BaiduMapUtil.searchPlace(keyWord, location.getBd09Lng(), location.getBd09Lat());
						// δ������POI
						if (null == placeList || 0 == placeList.size()) {
							respContent = String.format("/�ѹ��������͵�λ�ø���δ��������%s����Ϣ�� ",keyWord);
						} else {
							List<Article> articleList = BaiduMapUtil.makeArticleList(placeList, location.getBd09Lng(), location.getBd09Lat());
							// �ظ�ͼ����Ϣ
							NewsMessage newsMessage = new NewsMessage();
							newsMessage.setToUserName(fromUserName);
							newsMessage.setFromUserName(toUserName);
							newsMessage.setCreateTime(new Date().getTime());
							newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
							newsMessage.setArticles(articleList);
							newsMessage.setArticleCount(articleList.size());
							respXML = MessageUtil.messageToXml(newsMessage);
						}
					}
					return respXML;
				}
				
				//���û����������ı���Ϣʱ
				else{
					respContent=ChatService.chat(fromUserName, createTime, content);
				}
			}
			// ͼƬ��Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				// ȡ��ͼƬ��ַ  
                String picUrl = requestMap.get("PicUrl");  
                // ����ʶ��  
                String detectResult = FaceService.detect(picUrl);  
				respContent = detectResult;
			}
			// ������Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				respContent = "�����͵���������Ϣ��";
			}
			// ��Ƶ��Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
				respContent = "�����͵�����Ƶ��Ϣ��";
			}
			// ����λ����Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				// �û����͵ľ�γ��
				String lng = requestMap.get("Location_Y");
				String lat = requestMap.get("Location_X");
				// ����ת����ľ�γ��
				String bd09Lng = null;
				String bd09Lat = null;
				// ���ýӿ�ת������
				UserLocation userLocation = BaiduMapUtil.convertCoord(lng, lat);
				if (null != userLocation) {
					bd09Lng = userLocation.getBd09Lng();
					bd09Lat = userLocation.getBd09Lat();
				}
				// �����û�����λ��
				MySQLUtil.saveUserLocation(fromUserName, lng, lat, bd09Lng, bd09Lat);

				StringBuffer buffer = new StringBuffer();
				buffer.append("[���]").append("�ɹ���������λ�ã�").append("\n\n");
				buffer.append("���������������ؼ��ʻ�ȡ�ܱ���Ϣ�ˣ����磺").append("\n");
				buffer.append("        ����ATM").append("\n");
				buffer.append("        ����KTV").append("\n");
				buffer.append("        ��������").append("\n");
				buffer.append("�����ԡ������������ֿ�ͷ��");
				respContent = buffer.toString();
			}
			// ������Ϣ
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				respContent = "�����͵���������Ϣ��";
			}
			
			// �¼�����
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// �¼�����
				String eventType = requestMap.get("Event");
				// ��ע
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					respContent = getSubscribeMsg();
				}
				// ȡ����ע
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					// TODO ȡ�����ĺ��û����ղ������ںŷ��͵���Ϣ����˲���Ҫ�ظ���Ϣ
				}
				// ɨ���������ά��
				else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
					// TODO ����ɨ���������ά���¼�
				}
				// �ϱ�����λ��
				else if (eventType.equals(MessageUtil.EVENT_TYPE_LOCATION)) {
					// TODO �����ϱ�����λ���¼�
				}
				// �Զ���˵�����¼�
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					// �¼�keyֵ�ж��û�����İ�ť
					String eventKey = requestMap.get("EventKey");
					// ����keyֵ�ж��û�����İ�ť
					if (eventKey.equals("timetable")) {
						Article article = new Article();
						article.setTitle("��Դ�й�");
						article.setDescription("��Դ�й�����������2008��8�£���Ŀǰ�й����Ŀ�Դ����������");
						article.setPicUrl("");
						article.setUrl("http://m.oschina.net");
						List<Article> articleList = new ArrayList<Article>();
						articleList.add(article);
						// ����ͼ����Ϣ
						NewsMessage newsMessage = new NewsMessage();
						newsMessage.setToUserName(fromUserName);
						newsMessage.setFromUserName(toUserName);
						newsMessage.setCreateTime(new Date().getTime());
						newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
						newsMessage.setArticleCount(articleList.size());
						newsMessage.setArticles(articleList);
						respXML = MessageUtil.messageToXml(newsMessage);
					} 
					else if (eventKey.equals("weather")) {
						respContent=getWeatherUsage();
					}
					else if (eventKey.equals("bus")) {
						respContent=getBusUsage();
					}
					else if (eventKey.equals("delivery")) {
						respContent=getDeliveryUsage();
					}	
					else if (eventKey.equals("translate")) {
						respContent=getTranslateUsage();
					}
					else if (eventKey.equals("todayInHistory")) {
						respContent=TodayInHistoryService.getTodayInHistoryInfo();
					}	
					else if (eventKey.equals("news")) {
						respContent=getNewsUsage();
					}
					else if (eventKey.equals("search")) {
						respContent=getSearchUsage();
					}	
					else if (eventKey.equals("face")) {
						respContent=getFaceUsage();
					}
					else if (eventKey.equals("robot")) {
						respContent=getRobotUsage();
					}		
				}
			}
			if(null != respContent){
				// �����ı���Ϣ������
				textMessage.setContent(respContent);
				// ���ı���Ϣ����ת����xml
				respXML = MessageUtil.messageToXml(textMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respXML;
	}
	
	/**
	 * ��ע��ʾ��
	 * 
	 * @return
	 */
	private static String getSubscribeMsg() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("��ӭ��ע��[���]").append("\n\n");
		buffer.append("hurС��Ϊ���ṩУ԰������������������վ�������������ֵĺð��֣�").append("\n\n");
		buffer.append("���ݿ�ݲ˵���������").append("\n");
		buffer.append("ôô��~");
		return buffer.toString();
	}
	
	/**
	 * ����Ԥ������ָ��
	 * 
	 * @return
	 */
	public static String getWeatherUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("����Ԥ������ָ��").append("\n\n");
		buffer.append("�ظ�������+��������").append("\n");
		buffer.append("���磺�����ϲ�");
		return buffer.toString();
	}
	
	/**
	 * ������ѯ����ָ��
	 * 
	 * @return
	 */
	public static String getBusUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("������ѯ����ָ��").append("\n\n");
		buffer.append("��ѯ���й�����·").append("\n");
		buffer.append("��ʽ�����У���·����").append("\n");
		buffer.append("���磺�ϲ���232·").append("\n\n");
		buffer.append("��ѯ���й����ݳ˷���").append("\n");
		buffer.append("��ʽ�����У�������յ�").append("\n");
		buffer.append("���磺�ϲ����ƴ���®������վ");
		return buffer.toString();
	}
	
	/**
	 * ��ݲ�ѯ����ָ��
	 * 
	 * @return
	 */
	public static String getDeliveryUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("��ݲ�ѯ����ָ��").append("\n\n");
		buffer.append("�ظ�����ݿ�ݹ�˾����ݵ���").append("\n");
		buffer.append("���磺���˳�ᣬ976186294981");
		return buffer.toString();
	}
	
	/**
	 * ����ʵ����ָ��
	 * 
	 * @return
	 */
	public static String getTranslateUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\ue148").append("����ʵ����ָ��").append("\n\n");
		buffer.append("�ظ�������+��Ҫ����Ĵ�������").append("\n");
		buffer.append("���磺�����Ұ���");
		return buffer.toString();
	}
	
	/**
	 * �ȵ����Ų���ָ��
	 * 
	 * @return
	 */
	public static String getNewsUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\ue148").append("�ȵ����Ų���ָ��").append("\n\n");
		buffer.append("�ظ�����������+����").append("\n");
		buffer.append("���磺��������");
		return buffer.toString();
	}
	
	/**
	 * �ܱ���������ָ��
	 * 
	 *  @return
	 */
	public static String getSearchUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("�ܱ���������ָ��").append("\n\n");
		buffer.append("������ڵײ���\"+\"��ť��ѡ��\"λ��\"����\"����\"");
		return buffer.toString();
	}
	
	/**
	 * ����ʶ�����ָ��
	 * 
	 * @return
	 */
	public static String getFaceUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("����������ָ��").append("\n\n");
		buffer.append("����һ����������Ƭ��hur���ܰ�����������塢���䡢�Ա����ϢŶ~").append("\n\n");
		buffer.append("������ڵײ���\"+\"��ť��ѡ��\"ͼƬ\"���������ѡ��һ����������\"����\"").append("\n\n");
		buffer.append("�����������ǲ��ǳ���̫�ż���[��Ц]");
		return buffer.toString();
	}
	
	/**
	 * ��������˲���ָ��
	 * 
	 * @return
	 */
	public static String getRobotUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("��������˲���ָ��").append("\n\n");
		buffer.append("��Ͼ���ģ�����hur����ɣ�hur�����ĵģ����ʱش�").append("\n");
		buffer.append("���磺").append("\n");
		buffer.append("����Ц��").append("\n");
		buffer.append("��Ʊ�绰").append("\n");
		buffer.append("�ϲ���ʲô����ĵط�");
		return buffer.toString();
	}	
}


