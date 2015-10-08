package com.ibs.zj.javamail.mime;

import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MimeMail {

	public static void main(String[] args) throws Exception{
		// 服务器属性设置
		Properties properties = new Properties();
		properties.setProperty("mail.host", "mail.ibs-tech.com.cn");
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "true");
		// 发送邮件的6个步骤
		// 1、创建Session
		Session session = Session.getInstance(properties);
		// 开启Session的debug模式，查看邮件发送的状态
		session.setDebug(true);
		// 2、通过Session得到transport对象
		Transport ts = session.getTransport();
		// 3、使用邮箱的用户名和密码(发件人的用户名和密码)连上邮件服务器，
		//   发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，
		//   用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		ts.connect("mail.ibs-tech.com.cn", "zhongjun", "ruiyuan");
		// 4、创建邮件
		// 纯文本邮件
		// Message message = createMessage(session); 
		// 图片邮件
		Message message = createImageMessage(session);  
		// 附件邮件
		// Message message = createAttachMessage(session);
		// 复杂邮件
		// Message message = createMixedMessage(session);
		
		
		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		// 6、关闭
		ts.close();
		
	}

	/**
	 * 复杂邮件
	 * 
	 * @param session
	 * @return
	 */
	private static MimeMessage createMixedMessage(Session session) throws Exception{
		// 创建邮件
		MimeMessage message = new MimeMessage(session);
		// 设置邮件的基本信息
		// 发件人
		message.setFrom(new InternetAddress("zhongjun@ibs-tech.com.cn"));
		// 收件人
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("1065127135@qq.com"));
		// 邮件标题
		message.setSubject("复杂邮件");
		
		//正文
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("xxx这是女的xxxx<br/><img src='cid:taa.jpg'>","text/html;charset=UTF-8");
        
        //图片
        MimeBodyPart image = new MimeBodyPart();
        image.setDataHandler(new DataHandler(new FileDataSource("src\\main\\resources\\bbb.jpg")));
        image.setContentID("taa.jpg");
        
        //附件1
        MimeBodyPart attach = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource("src\\main\\resources\\xx.zip"));
        attach.setDataHandler(dh);
        attach.setFileName(dh.getName());
        
        //附件2
        MimeBodyPart attach2 = new MimeBodyPart();
        DataHandler dh2 = new DataHandler(new FileDataSource("src\\main\\resources\\yy.zip"));
        attach2.setDataHandler(dh2);
        attach2.setFileName(MimeUtility.encodeText(dh2.getName()));
        
        //描述关系:正文和图片
        MimeMultipart mp1 = new MimeMultipart();
        mp1.addBodyPart(text);
        mp1.addBodyPart(image);
        mp1.setSubType("related");
        
        //描述关系:正文和附件
        MimeMultipart mp2 = new MimeMultipart();
        mp2.addBodyPart(attach);
        mp2.addBodyPart(attach2);
        
        //代表正文的bodypart
        MimeBodyPart content = new MimeBodyPart();
        content.setContent(mp1);
        mp2.addBodyPart(content);
        mp2.setSubType("mixed");
        
        message.setContent(mp2);
        message.saveChanges();
        
        message.writeTo(new FileOutputStream("E:\\MixedMail.eml"));
        //返回创建好的的邮件
        return message;
	}

	/**
	 * 创建附件邮件
	 * 
	 * @param session
	 * @return
	 */
	private static MimeMessage createAttachMessage(Session session)
			throws Exception {
		// 创建邮件
		MimeMessage message = new MimeMessage(session);
		// 设置邮件的基本信息
		// 发件人
		message.setFrom(new InternetAddress("zhongjun@ibs-tech.com.cn"));
		// 收件人
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("1065127135@qq.com"));
		// 邮件标题
		message.setSubject("附件邮件");

		
		// 创建邮件正文，为了避免邮件正文中文乱码问题，需要使用charset=UTF-8指明字符编码
		MimeBodyPart text = new MimeBodyPart();
		text.setContent("使用JavaMail创建的带附件的邮件", "text/html;charset=UTF-8");

		// 创建邮件附件
		MimeBodyPart attach = new MimeBodyPart();
		DataHandler dh = new DataHandler(new FileDataSource("src\\main\\resources\\desktop.jpg"));
		attach.setDataHandler(dh);
		attach.setFileName(dh.getName()); //

		// 创建容器描述数据关系
		MimeMultipart mp = new MimeMultipart();
		mp.addBodyPart(text);
		mp.addBodyPart(attach);
		mp.setSubType("mixed");

		message.setContent(mp);
		message.saveChanges();
		// 将创建的Email写入到E盘存储
		message.writeTo(new FileOutputStream("E:\\attachMail.eml"));
		// 返回生成的邮件
		return message;
	}

	/**
	 * 创建图片邮件
	 * 
	 * @param session
	 * @return
	 */
	private static MimeMessage createImageMessage(Session session) throws Exception {
		// 创建邮件
		MimeMessage message = new MimeMessage(session);
		// 设置邮件的基本信息
		// 发件人
		message.setFrom(new InternetAddress("zhongjun@ibs-tech.com.cn"));
		// 收件人
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("1065127135@qq.com"));
		// 抄送
		message.setRecipient(Message.RecipientType.CC, new InternetAddress("zhongjun@ibs-tech.com.cn"));
		
		// 邮件标题
		message.setSubject("图片邮件");
		// 准备邮件数据
		// 准备邮件正文数据
		MimeBodyPart text = new MimeBodyPart();
		text.setContent("xxx这是女的xxxx<br/><img src='cid:taa.jpg'>","text/html;charset=UTF-8");
		// 准备图片数据
		MimeBodyPart image = new MimeBodyPart();
		DataHandler dh = new DataHandler(new FileDataSource("src\\main\\resources\\desktop.jpg"));
		image.setDataHandler(dh);
		image.setContentID("taa.jpg");
		// 描述数据关系
		MimeMultipart mm = new MimeMultipart();
		mm.addBodyPart(text);
		mm.addBodyPart(image);
		mm.setSubType("related");

		message.setContent(mm);
		message.saveChanges();
		// 将创建好的邮件写入到E盘以文件的形式进行保存
		message.writeTo(new FileOutputStream("E:\\ImageMail.eml"));
		// 返回创建好的邮件
		return message;
	}

	/**
	 * 创建纯文本邮件
	 * 
	 * @param session
	 * @return
	 * @throws MessagingException 
	 * @throws Exception 
	 */
	public static MimeMessage createMessage(Session session) throws Exception{
		// 创建邮件对象
		MimeMessage mimeMessage =  new MimeMessage(session);
		// 发件人
		mimeMessage.setFrom(new InternetAddress("zhongjun@ibs-tech.com.cn"));
		// 收件人
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("1065127135@qq.com"));
		// 主题
		mimeMessage.setSubject("纯文本");
		// 内容
		mimeMessage.setContent("你好啊，JAVAMAIL。", "text/html;charset=UTF-8");
		
		return mimeMessage;
	}
	
	
}
