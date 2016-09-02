package cn.hur.learn.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import cn.hur.learn.service.ChatService;
import cn.hur.learn.service.CoreService;
import cn.hur.learn.util.MessageUtil;
import cn.hur.learn.util.SignUtil;


/**
 * 核心请求处理类
 * 
 */
public class CoreServlet extends HttpServlet {
	private static final long serialVersionUID = 4440739483644821986L;

	/**
	 * 请求校验（确认请求来自微信服务器）
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");

		PrintWriter out = response.getWriter();
		// 请求校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		out = null;
	}

	/**
	 * 处理微信服务器发来的消息
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		System.out.println("---doPost-----");
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 接收参数微信加密签名、 时间戳、随机数
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		// 加解密类型
		String encryptType = request.getParameter("encrypt_type");
		System.out.println("encryptType:"+encryptType);
		
		try{
			PrintWriter out = response.getWriter();
			// 请求校验
			if (SignUtil.checkSignature(signature, timestamp, nonce)) {
				Map<String, String> requestMap = null;
				//加解密模式
				if("aes".equals(encryptType)){
					requestMap = MessageUtil.parseXmlCrypt(request);
					// 消息进行处理
					String respXml = CoreService.process(requestMap);
					// 加密
					respXml = MessageUtil.getWXBizMsgCrypt().encryptMsg(respXml, timestamp, nonce);
					out.print(respXml);
					System.out.println("respXml:"+respXml);
				}
				//明文模式
				else{
					requestMap = MessageUtil.parseXml(request);
					// 消息进行处理
					String respXml = CoreService.process(requestMap);
					out.print(respXml);
					System.out.println("respXml:"+respXml);
				}
			}
			out.close();
			out = null;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void init() throws ServletException {
		File indexDir = new File(ChatService.getIndexDir());
		// 如果索引目录不存在则创建索引
		if (!indexDir.exists())
			ChatService.createIndex();
	}

}


