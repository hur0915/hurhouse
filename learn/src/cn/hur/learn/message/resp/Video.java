package cn.hur.learn.message.resp;

/**
 * ��Ƶmodel
 * 
 */
public class Video {
	// ý���ļ�ID
	private String MediaId;
	// ͨ���زĹ����еĽӿ��ϴ���ý���ļ����õ���id
	private String ThumbMediaId;
	// ��Ƶ��Ϣ�ı���
	private String Title;
	// ��Ƶ��Ϣ������
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

