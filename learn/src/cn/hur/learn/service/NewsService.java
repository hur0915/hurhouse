package cn.hur.learn.service;

import java.util.ArrayList;
import java.util.List;

import cn.hur.learn.message.resp.Article;
import cn.hur.learn.pojo.BaiduPlace;
import cn.hur.learn.pojo.News;
import cn.hur.learn.util.CommonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 新闻头条聚合数据API接口操作类
 *
 */
public class NewsService {
	
    /**
     * 根据关键字检索
     */
    public static List<News> getNewsInfo(String type) throws Exception{
        String url ="http://v.juhe.cn/toutiao/index?type=TYPE&key=KEY";//请求接口地址
        // 对URL进行编码  
        url = url.replace("TYPE", CommonUtil.urlEncode(type,"utf-8"));  
        url = url.replace("KEY", "0816739d2b1909321462e52adb49886c");  
        // 调用新闻头条接口  
        String json = CommonUtil.httpRequest(url, "GET", null);  
        // 解析返回json中的news列表  
        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONObject results = jsonObject.getJSONObject("result");
        JSONArray newsInfo = results.getJSONArray("data");

        List<News> newsList=new ArrayList<News>();
        
        for(int i=0;i<newsInfo.size();i++){
        	News newNews = new News();
        	newNews.setTitle(newsInfo.getJSONObject(i).getString("title"));
        	newNews.setThumbnail_pic(newsInfo.getJSONObject(i).getString("thumbnail_pic_s"));
        	newNews.setUrl(newsInfo.getJSONObject(i).getString("url"));
//        	newNews.setRealtype(newsInfo.getJSONObject(i).getString("realtype"));
        	newsList.add(newNews);
        	
		}
        return newsList;
    }
    
    /**
	 * 根据News组装图文列表
	 * 
	 * @return List<Article>
	 */
	public static List<Article> makeArticleList(List<News> newsList) {
		
		List<Article> list = new ArrayList<Article>();
		News news = null;
		for (int i = 0; i < newsList.size(); i++) {
			news = newsList.get(i);
			Article article = new Article();
			article.setTitle(news.getTitle());
			article.setUrl(news.getUrl());
			article.setPicUrl(news.getThumbnail_pic());
			list.add(article);
		}
		return list;
	}
    
    /** 
     * 本机测试
     * @param args 
     */  
    public static void main(String[] args) {  
        try {
			List<News> newsList = new ArrayList<News>();
			newsList = getNewsInfo("junshi");
			for(int i=0;i<newsList.size();i++){
				System.out.println("-----------"+i+"-------------");
				System.out.println(newsList.get(i).getTitle());
//				System.out.println(newsList.get(i).getRealtype());
				System.out.println(newsList.get(i).getThumbnail_pic());
				System.out.println(newsList.get(i).getUrl());
				System.out.println("-------------------------");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    } 
}
