package cn.hur.learn.pojo;

/**
 * 新闻头条
 *
 */
public class News {
	
	private String title;//标题
	private String thumbnail_pic;//图片地址
	private String url;//链接地址
	private String realtype;//类型
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail_pic() {
		return thumbnail_pic;
	}
	public void setThumbnail_pic(String thumbnail_pic) {
		this.thumbnail_pic = thumbnail_pic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRealtype() {
		return realtype;
	}
	public void setRealtype(String realtype) {
		this.realtype = realtype;
	}
}
