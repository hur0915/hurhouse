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
 * 核心业务类
 * 
 */
public class CoreService {
	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String process(Map<String, String> requestMap) {
		// XML格式的消息数据
		String respXML = null;
		// 默认返回的文本消息内容
		String respContent =null;
		try {
			// 发送方账号
			String fromUserName = requestMap.get("FromUserName");
			// 开发者微信号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			// 消息创建时间
			String createTime = requestMap.get("CreateTime");

			// 回复文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				// 获取用户发出的文本信息
				String content=requestMap.get("Content").trim();
				// 对用户的文本消息进行匹配
	
				// 开头为翻译，则进行翻译词典
				if(content.startsWith("翻译")){
					String keyWord = content.replaceAll("^翻译", "").trim();
					if ("".equals(keyWord)) {
						respContent=getTranslateUsage();
					} 
					else {
						respContent=TranslateService.translate(keyWord);
					}
				}
				
				// 开头为天气，则进行天气查询
				else if(content.startsWith("天气") || content.endsWith("天气")){
					String keyWord = content.replaceAll("^天气", "").trim();
					if ("".equals(keyWord)) {
						respContent=getWeatherUsage();
					} 
					else {
						respContent=WeatherService.getWeatherInfo(keyWord);
					}
				}
				
				// 开头为快递，则进行快递查询
				else if(content.startsWith("快递")){
					String keyWord = content.replaceAll("^快递", "").trim();
					if ("".equals(keyWord)) {
						respContent=getDeliveryUsage();
					} 
					else {
						respContent=DeliveryService.getDeliveryInfo(keyWord);
					}
				}
				
				// 开头为新闻，则进行新闻查询
				else if(content.startsWith("新闻") || content.endsWith("新闻")){
					String keyWord = content.replaceAll("^新闻", "").trim();
					if ("".equals(keyWord)) {
						respContent=getNewsUsage();
					} 
					else {
						List<News> newsList=NewsService.getNewsInfo(keyWord);
						if(null == newsList || 0 == newsList.size()){
							respContent = "对不起，未查询到相关新闻！";
						}else{
							List<Article> articleList = NewsService.makeArticleList(newsList);
							// 回复图文信息
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
				
				// 输入历史上的今天，进行查找
				else if(content.startsWith("历史上的今天")){
					respContent=TodayInHistoryService.getTodayInHistoryInfo();
				}
				
				
				// 开头为附近,则进行周边搜索
				else if (content.startsWith("附近")) {
					String keyWord = content.replaceAll("附近", "").trim();
					// 获取用户最后一次发送的地理位置
					UserLocation location = MySQLUtil.getLastLocation(fromUserName);
					// 未获取到
					if (null == location) {
						respContent = getSearchUsage();
					} else {
						// 根据转换后（纠偏）的坐标搜索周边POI
						List<BaiduPlace> placeList = BaiduMapUtil.searchPlace(keyWord, location.getBd09Lng(), location.getBd09Lat());
						// 未搜索到POI
						if (null == placeList || 0 == placeList.size()) {
							respContent = String.format("/难过，您发送的位置附近未搜索到“%s”信息！ ",keyWord);
						} else {
							List<Article> articleList = BaiduMapUtil.makeArticleList(placeList, location.getBd09Lng(), location.getBd09Lat());
							// 回复图文信息
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
				
				//当用户发送其他文本消息时
				else{
					respContent=ChatService.chat(fromUserName, createTime, content);
				}
			}
			// 图片消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				// 取得图片地址  
                String picUrl = requestMap.get("PicUrl");  
                // 人脸识别  
                String detectResult = FaceService.detect(picUrl);  
				respContent = detectResult;
			}
			// 语音消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				respContent = "您发送的是语音消息！";
			}
			// 视频消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
				respContent = "您发送的是视频消息！";
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				// 用户发送的经纬度
				String lng = requestMap.get("Location_Y");
				String lat = requestMap.get("Location_X");
				// 坐标转换后的经纬度
				String bd09Lng = null;
				String bd09Lat = null;
				// 调用接口转换坐标
				UserLocation userLocation = BaiduMapUtil.convertCoord(lng, lat);
				if (null != userLocation) {
					bd09Lng = userLocation.getBd09Lng();
					bd09Lat = userLocation.getBd09Lat();
				}
				// 保存用户地理位置
				MySQLUtil.saveUserLocation(fromUserName, lng, lat, bd09Lng, bd09Lat);

				StringBuffer buffer = new StringBuffer();
				buffer.append("[愉快]").append("成功接收您的位置！").append("\n\n");
				buffer.append("您可以输入搜索关键词获取周边信息了，例如：").append("\n");
				buffer.append("        附近ATM").append("\n");
				buffer.append("        附近KTV").append("\n");
				buffer.append("        附近厕所").append("\n");
				buffer.append("必须以“附近”两个字开头！");
				respContent = buffer.toString();
			}
			// 链接消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				respContent = "您发送的是链接消息！";
			}
			
			// 事件推送
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				// 关注
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					respContent = getSubscribeMsg();
				}
				// 取消关注
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
				}
				// 扫描带参数二维码
				else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
					// TODO 处理扫描带参数二维码事件
				}
				// 上报地理位置
				else if (eventType.equals(MessageUtil.EVENT_TYPE_LOCATION)) {
					// TODO 处理上报地理位置事件
				}
				// 自定义菜单点击事件
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					// 事件key值判断用户点击的按钮
					String eventKey = requestMap.get("EventKey");
					// 根据key值判断用户点击的按钮
					if (eventKey.equals("timetable")) {
						Article article = new Article();
						article.setTitle("开源中国");
						article.setDescription("开源中国社区成立于2008年8月，是目前中国最大的开源技术社区。");
						article.setPicUrl("");
						article.setUrl("http://m.oschina.net");
						List<Article> articleList = new ArrayList<Article>();
						articleList.add(article);
						// 创建图文消息
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
				// 设置文本消息的内容
				textMessage.setContent(respContent);
				// 将文本消息对象转换成xml
				respXML = MessageUtil.messageToXml(textMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respXML;
	}
	
	/**
	 * 关注提示语
	 * 
	 * @return
	 */
	private static String getSubscribeMsg() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("欢迎关注！[愉快]").append("\n\n");
		buffer.append("hur小屋为您提供校园服务、生活服务和休闲驿站，是您生活娱乐的好帮手！").append("\n\n");
		buffer.append("根据快捷菜单快快体验吧").append("\n");
		buffer.append("么么哒~");
		return buffer.toString();
	}
	
	/**
	 * 天气预报操作指南
	 * 
	 * @return
	 */
	public static String getWeatherUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("天气预报操作指南").append("\n\n");
		buffer.append("回复：天气+城市名称").append("\n");
		buffer.append("例如：天气南昌");
		return buffer.toString();
	}
	
	/**
	 * 公交查询操作指南
	 * 
	 * @return
	 */
	public static String getBusUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("公交查询操作指南").append("\n\n");
		buffer.append("查询城市公交线路").append("\n");
		buffer.append("格式：城市，线路名称").append("\n");
		buffer.append("例如：南昌，232路").append("\n\n");
		buffer.append("查询城市公交驾乘方案").append("\n");
		buffer.append("格式：城市，起点至终点").append("\n");
		buffer.append("例如：南昌，财大麦庐至高铁站");
		return buffer.toString();
	}
	
	/**
	 * 快递查询操作指南
	 * 
	 * @return
	 */
	public static String getDeliveryUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("快递查询操作指南").append("\n\n");
		buffer.append("回复：快递快递公司，快递单号").append("\n");
		buffer.append("例如：快递顺丰，976186294981");
		return buffer.toString();
	}
	
	/**
	 * 翻译词典操作指南
	 * 
	 * @return
	 */
	public static String getTranslateUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\ue148").append("翻译词典操作指南").append("\n\n");
		buffer.append("回复：翻译+需要翻译的词语或句子").append("\n");
		buffer.append("例如：翻译我爱你");
		return buffer.toString();
	}
	
	/**
	 * 热点新闻操作指南
	 * 
	 * @return
	 */
	public static String getNewsUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\ue148").append("热点新闻操作指南").append("\n\n");
		buffer.append("回复：新闻类型+新闻").append("\n");
		buffer.append("例如：娱乐新闻");
		return buffer.toString();
	}
	
	/**
	 * 周边搜索操作指南
	 * 
	 *  @return
	 */
	public static String getSearchUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("周边搜索操作指南").append("\n\n");
		buffer.append("点击窗口底部的\"+\"按钮，选择\"位置\"，点\"发送\"");
		return buffer.toString();
	}
	
	/**
	 * 人脸识别操作指南
	 * 
	 * @return
	 */
	public static String getFaceUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("人脸检测操作指南").append("\n\n");
		buffer.append("发送一张清晰的照片，hur就能帮你分析出种族、年龄、性别等信息哦~").append("\n\n");
		buffer.append("点击窗口底部的\"+\"按钮，选择\"图片\"，在相册里选择一张人脸，点\"发送\"").append("\n\n");
		buffer.append("快来试试你是不是长得太着急了[坏笑]");
		return buffer.toString();
	}
	
	/**
	 * 聊天机器人操作指南
	 * 
	 * @return
	 */
	public static String getRobotUsage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("聊天机器人操作指南").append("\n\n");
		buffer.append("闲暇无聊，来找hur聊天吧，hur很能聊的，有问必答！").append("\n");
		buffer.append("例如：").append("\n");
		buffer.append("讲个笑话").append("\n");
		buffer.append("订票电话").append("\n");
		buffer.append("南昌有什么好玩的地方");
		return buffer.toString();
	}	
}


