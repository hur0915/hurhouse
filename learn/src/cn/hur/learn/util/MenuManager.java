package cn.hur.learn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hur.learn.menu.Button;
import cn.hur.learn.menu.ClickButton;
import cn.hur.learn.menu.ComplexButton;
import cn.hur.learn.menu.Menu;
import cn.hur.learn.menu.ViewButton;
import cn.hur.learn.pojo.Token;
import cn.hur.learn.util.CommonUtil;
import cn.hur.learn.util.MenuUtil;

/**
 * �˵���������
 * 
 */
public class MenuManager {
	private static Logger log = LoggerFactory.getLogger(MenuManager.class);

	/**
	 * ����˵��ṹ
	 * 
	 * @return
	 */
	private static Menu getMenu() {
		ClickButton btn11 = new ClickButton();
		btn11.setName("ѧУ��̬");
		btn11.setType("click");
		btn11.setKey("http://www.jxufe.edu.cn/");

		ViewButton btn12 = new ViewButton();
		btn12.setName("��������ѯ");
		btn12.setType("view");
		btn12.setUrl("http://cet.99sushe.com/");

		ViewButton btn13 = new ViewButton();
		btn13.setName("�����г�");
		btn13.setType("view");
		btn13.setUrl("http://192.168.0.102:8080/shop/index.action");

		ClickButton btn21 = new ClickButton();
		btn21.setName("����Ԥ��");
		btn21.setType("click");
		btn21.setKey("weather");

		ClickButton btn22 = new ClickButton();
		btn22.setName("�ȵ�����");
		btn22.setType("click");
		btn22.setKey("news");

		ClickButton btn23 = new ClickButton();
		btn23.setName("����ٲ�");
		btn23.setType("click");
		btn23.setKey("delivery");
		
		ClickButton btn24 = new ClickButton();
		btn24.setName("����ʵ�");
		btn24.setType("click");
		btn24.setKey("translate");
		
		ClickButton btn25 = new ClickButton();
		btn25.setName("�ܱ�����");
		btn25.setType("click");
		btn25.setKey("search");

		ClickButton btn31 = new ClickButton();
		btn31.setName("��ʷ�ϵĽ���");
		btn31.setType("click");
		btn31.setKey("todayInHistory");

		ClickButton btn32 = new ClickButton();
		btn32.setName("����ʶ��");
		btn32.setType("click");
		btn32.setKey("face");

		ClickButton btn33 = new ClickButton();
		btn33.setName("���������");
		btn33.setType("click");
		btn33.setKey("robot");
		
		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("У԰����");
		mainBtn1.setSub_button(new Button[] { btn11, btn12, btn13 });

		ComplexButton mainBtn2 = new ComplexButton();
		mainBtn2.setName("��������");
		mainBtn2.setSub_button(new Button[] { btn21, btn22, btn23, btn24, btn25 });

		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("������վ");
		mainBtn3.setSub_button(new Button[] { btn31, btn32, btn33 });

		Menu menu = new Menu();
		menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

		return menu;
	}

	public static void main(String[] args) {
		// �������û�Ψһƾ֤
		String appId = "wxfddda94c92627e17";
		// �������û�Ψһƾ֤��Կ
		String appSecret = "2664a5cc1961fe3e60950eae5e560503";

		// ���ýӿڻ�ȡƾ֤
		Token token = CommonUtil.getToken(appId, appSecret);

		if (null != token) {
			// �����˵�
			boolean result = MenuUtil.createMenu(getMenu(), token.getAccessToken());

			// �жϲ˵��������
			if (result)
				log.info("�˵������ɹ���");
			else
				log.info("�˵�����ʧ�ܣ�");
		}
	}
}

