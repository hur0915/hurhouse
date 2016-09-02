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
 * 菜单管理器类
 * 
 */
public class MenuManager {
	private static Logger log = LoggerFactory.getLogger(MenuManager.class);

	/**
	 * 定义菜单结构
	 * 
	 * @return
	 */
	private static Menu getMenu() {
		ClickButton btn11 = new ClickButton();
		btn11.setName("学校动态");
		btn11.setType("click");
		btn11.setKey("http://www.jxufe.edu.cn/");

		ViewButton btn12 = new ViewButton();
		btn12.setName("四六级查询");
		btn12.setType("view");
		btn12.setUrl("http://cet.99sushe.com/");

		ViewButton btn13 = new ViewButton();
		btn13.setName("跳蚤市场");
		btn13.setType("view");
		btn13.setUrl("http://192.168.0.102:8080/shop/index.action");

		ClickButton btn21 = new ClickButton();
		btn21.setName("天气预报");
		btn21.setType("click");
		btn21.setKey("weather");

		ClickButton btn22 = new ClickButton();
		btn22.setName("热点新闻");
		btn22.setType("click");
		btn22.setKey("news");

		ClickButton btn23 = new ClickButton();
		btn23.setName("快递速查");
		btn23.setType("click");
		btn23.setKey("delivery");
		
		ClickButton btn24 = new ClickButton();
		btn24.setName("翻译词典");
		btn24.setType("click");
		btn24.setKey("translate");
		
		ClickButton btn25 = new ClickButton();
		btn25.setName("周边搜索");
		btn25.setType("click");
		btn25.setKey("search");

		ClickButton btn31 = new ClickButton();
		btn31.setName("历史上的今天");
		btn31.setType("click");
		btn31.setKey("todayInHistory");

		ClickButton btn32 = new ClickButton();
		btn32.setName("人脸识别");
		btn32.setType("click");
		btn32.setKey("face");

		ClickButton btn33 = new ClickButton();
		btn33.setName("聊天机器人");
		btn33.setType("click");
		btn33.setKey("robot");
		
		ComplexButton mainBtn1 = new ComplexButton();
		mainBtn1.setName("校园服务");
		mainBtn1.setSub_button(new Button[] { btn11, btn12, btn13 });

		ComplexButton mainBtn2 = new ComplexButton();
		mainBtn2.setName("生活助手");
		mainBtn2.setSub_button(new Button[] { btn21, btn22, btn23, btn24, btn25 });

		ComplexButton mainBtn3 = new ComplexButton();
		mainBtn3.setName("休闲驿站");
		mainBtn3.setSub_button(new Button[] { btn31, btn32, btn33 });

		Menu menu = new Menu();
		menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

		return menu;
	}

	public static void main(String[] args) {
		// 第三方用户唯一凭证
		String appId = "wxfddda94c92627e17";
		// 第三方用户唯一凭证密钥
		String appSecret = "2664a5cc1961fe3e60950eae5e560503";

		// 调用接口获取凭证
		Token token = CommonUtil.getToken(appId, appSecret);

		if (null != token) {
			// 创建菜单
			boolean result = MenuUtil.createMenu(getMenu(), token.getAccessToken());

			// 判断菜单创建结果
			if (result)
				log.info("菜单创建成功！");
			else
				log.info("菜单创建失败！");
		}
	}
}

