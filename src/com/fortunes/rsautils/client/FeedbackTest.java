package com.fortunes.rsautils.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fortunes.rsautils.model.SocketModel;
import com.fortunes.rsautils.util.AESCrypt;
import com.fortunes.rsautils.util.AppConstants;
import com.fortunes.rsautils.util.RSAUtil;
import com.fortunes.rsautils.util.SocketTools;
import com.fortunes.rsautils.util.Util;

public class FeedbackTest {

	private static Logger log = Logger.getLogger(FeedbackTest.class);
	
	/**
	 * 返回的aesKey
	 * @param ip
	 * @param port
	 * @param outAesKey
	 */
	@SuppressWarnings("static-access")
	public static String getOutFeedback(String ip, int port ,String virtualNo,String outKey) throws Exception,IllegalAccessError{
		String outAesKey = null;
		Socket socket = null;
		try{
			socket = new Socket(ip,port);
			SocketTools tools = new SocketTools();
			
			File f = new File("src/com/fang/conf/file/8001模板.txt");//反馈文件路径
//			File f = new File("src/com/fang/conf/file/8001.txt");//反馈文件路径
			String feebackFile = Util.getFileToStr(f);
			System.out.println(feebackFile);
			String bytes = RSAUtil.bytesToHexString((AppConstants.TRADENO_8001 + virtualNo + AppConstants.COMMONNO).getBytes())+feebackFile  ; 
			
			System.out.println("xxxx----:"+new String (RSAUtil.hexStringToBytes(bytes),"GBK"));
			
			//AES加密
			AESCrypt aes = new AESCrypt() ;
			System.out.println("yuan数字符串长度-----："+bytes.length());
			int length = RSAUtil.hexStringToBytes(bytes).length;
			System.out.println("yuan数字符串长度-----："+length);
			int len = length/16 ;
			int plus = length % 16;
			byte[] crypt = null;
			if(plus != 0){ // 有余数,需要在后面补空格，直至为16的倍数
				crypt =	RSAUtil.hexStringToBytes(getFixedLenBlank(bytes,  16 * (len + 1)));
			}
			if(crypt!=null){
			  System.out.println("组成的16倍数字符串"+ crypt.length);
			}else{
			  System.out.println("组成的16倍数字符串"+ 0);
			}
			
			//加密
			byte[] b = RSAUtil.hexStringToBytes(outKey);
			b = Util.subBytes(b, 0, 16);
			System.out.println("密钥-------："+RSAUtil.bytesToHexString(b));
			System.out.println("");
			crypt = aes.encryptTwo(crypt, b); 
			String first = "0" + Util.getFixedLenZero(Integer.toString(16 * (len + 1)) , "0", 9)  ;
			byte[] encrypt = Util.combineTowBytes(first.getBytes(),crypt);  //已加密，并要发送的数据
//			System.out.println("16进制："	+ RSAUtil.bytesToHexString(encrypt));
			
//			Socket mySocket = new Socket(ip, port);
//			mySocket.
			 //发送至服务器端
			tools.writeIn(socket, encrypt);
			SocketModel model = tools.read(socket);  //接收并读入
			String mesg = model.getMessage();
			System.out.println("model.getMessage------"+mesg);
			if (model != null && model.isReadSuccess()) {
			  byte[] by = RSAUtil.hexStringToBytes(mesg);
			  String str = new String(by);
				System.err.println(str);
			}else{
				log.error( "数据读取失败！" );
				return "数据读取失败！";
			}
		} catch (Exception e) {
			log.error("通讯异常！");
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					log.error( "socket关闭异常："+ e);
					e.printStackTrace();
				}
			}
		}
		return outAesKey;
	}
	
	private static String getFixedLenBlank(String oldStr, int length) {
		if (null == oldStr || "".equals(oldStr)) {
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < length; i++) {
				buf.append(20);
			}
			return buf.toString();
		}
		
		oldStr = oldStr.trim();
		int oldLength = RSAUtil.hexStringToBytes(oldStr).length;

		if (oldLength > length) {
			StringBuilder buf = new StringBuilder(oldStr);
			buf.setLength(length);
			return buf.toString();
		}

		StringBuilder buf = new StringBuilder(oldStr);
		for (int i = 0; i < length - RSAUtil.hexStringToBytes(oldStr).length; i++) {
			buf.append(20);
		}
		return buf.toString();
	}

	public static void main(String[] args) throws IllegalAccessError, Exception {
	  InputStream is = FeedbackTest.class.getClassLoader().getResourceAsStream("com/fang/conf/file/http.properties");
	  Properties p = new Properties();
	  p.load(is);
	  String ip = p.getProperty("ip");
	  String port = p.getProperty("port");
	  int iPort = Integer.valueOf(port);
		OutAesKye out = new OutAesKye();
		String  outKey = out.getOutAesKey(ip, iPort, "111222333444","src/com/fang/conf/rsaKey/rsa_public_key.key","src/com/fang/conf/rsaKey/pkcs8_rsa_private_key.key");
		System.err.println("outKey:"+outKey);
		getOutFeedback(ip, iPort, "111222333444",outKey);
	}
}
