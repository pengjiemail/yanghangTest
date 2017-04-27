/**
 * File：Test.java
 * Package：com.fortunes.rsautils.client
 * Author：pengjie
 * Date：2017-4-1 上午9:13:35
 * Copyright (C) 2003-2017 搜房资讯有限公司-版权所有
 */
package com.fortunes.rsautils.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fortunes.rsautils.util.RSAUtil;


/**
 * 说明
 * @author pengjie
 */
public class Test {
  public static void main(String[] args) throws Exception {
    File f = new File("src/com/fang/conf/file/1.txt");
    String str = getFileToStr(f);
    System.out.println(str);
    String a = "11 2 （深圳）\r\ns s";
    String s = fixReportContent(a);
    System.out.println(s);
  }
  
  public static String getFileToStr(File file) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    InputStream in = new FileInputStream(file);
    int buf_size = 1024;
    byte[] buffer = new byte[buf_size];
    int len = 0;
    while (-1 != (len = in.read(buffer, 0, buf_size))) {
      bos.write(buffer, 0, len);
    }
    byte[] byteStr = bos.toByteArray();
    String message = RSAUtil.bytesToHexString(byteStr);
    return message;
  }
  
  public static  String fixReportContent(String reportContent) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    InputStream in = new ByteArrayInputStream(reportContent.getBytes());
    byte[] buffer = new byte[1024];
    int len = 0;
    while (-1 != (len = in.read(buffer, 0, 1024))) {
      bos.write(buffer, 0, len);
    }
    byte[] byteStr = bos.toByteArray();
    return RSAUtil.bytesToHexString(byteStr);
  }
}
