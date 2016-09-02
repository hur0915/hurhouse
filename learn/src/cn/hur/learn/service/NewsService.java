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
 * ����ͷ���ۺ�����API�ӿڲ�����
 *
 */
public class NewsService {
	
    /**
     * ���ݹؼ��ּ���
     */
    public static List<News> getNewsInfo(String type) throws Exception{
        String url ="http://v.juhe.cn/toutiao/index?type=TYPE&key=KEY";//����ӿڵ�ַ
        // ��URL���б���  
        url = url.replace("TYPE", CommonUtil.urlEncode(type,"utf-8"));  
        url = url.replace("KEY", "0816739d2b1909321462e52adb49886c");  
        // ��������ͷ���ӿ�  
        String json = CommonUtil.httpRequest(url, "GET", null);  
        // ��������json�е�news�б�  
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
	 * ����News��װͼ���б�
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
     * ��������
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
