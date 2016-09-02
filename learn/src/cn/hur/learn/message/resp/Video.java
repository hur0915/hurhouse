package cn.hur.learn.message.resp;

/**
 * 视频model
 * 
 */
public class Video {
	// 媒体文件ID
	private String MediaId;
	// 通过素材管理中的接口上传多媒体文件，得到的id
	private String ThumbMediaId;
	// 视频消息的标题
	private String Title;
	// 视频消息的描述
	private String Description;

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getThumbMediaId() {
		return ThumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		ThumbMediaId = thumbMediaId;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
}

