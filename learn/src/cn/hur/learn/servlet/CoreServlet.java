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
 * ������������
 * 
 */
public class CoreServlet extends HttpServlet {
	private static final long serialVersionUID = 4440739483644821986L;

	/**
	 * ����У�飨ȷ����������΢�ŷ�������
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// ΢�ż���ǩ��
		String signature = request.getParameter("signature");
		// ʱ���
		String timestamp = request.getParameter("timestamp");
		// �����
		String nonce = request.getParameter("nonce");
		// ����ַ���
		String echostr = request.getParameter("echostr");

		PrintWriter out = response.getWriter();
		// ����У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		out = null;
	}

	/**
	 * ����΢�ŷ�������������Ϣ
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		System.out.println("---doPost-----");
		// ��������Ӧ�ı��������ΪUTF-8����ֹ�������룩
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// ���ղ���΢�ż���ǩ���� ʱ����������
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		// �ӽ�������
		String encryptType = request.getParameter("encrypt_type");
		System.out.println("encryptType:"+encryptType);
		
		try{
			PrintWriter out = response.getWriter();
			// ����У��
			if (SignUtil.checkSignature(signature, timestamp, nonce)) {
				Map<String, String> requestMap = null;
				//�ӽ���ģʽ
				if("aes".equals(encryptType)){
					requestMap = MessageUtil.parseXmlCrypt(request);
					// ��Ϣ���д���
					String respXml = CoreService.process(requestMap);
					// ����
					respXml = MessageUtil.getWXBizMsgCrypt().encryptMsg(respXml, timestamp, nonce);
					out.print(respXml);
					System.out.println("respXml:"+respXml);
				}
				//����ģʽ
				else{
					requestMap = MessageUtil.parseXml(request);
					// ��Ϣ���д���
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
		// �������Ŀ¼�������򴴽�����
		if (!indexDir.exists())
			ChatService.createIndex();
	}

}


