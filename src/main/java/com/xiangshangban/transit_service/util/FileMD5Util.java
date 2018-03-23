package com.xiangshangban.transit_service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.log4j.Logger;

public class FileMD5Util {
	private static final Logger LOG = Logger.getLogger(FileMD5Util.class);
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
	 private final static String[] SaltDigits = {"." , "," , "/" , ";" , "!" , "@" , "#" , "$" , "%" , "&"};
	protected static MessageDigest messagedigest = null;
	static{
	   try{
	    messagedigest = MessageDigest.getInstance("MD5");
	   }catch(NoSuchAlgorithmException nsaex){
		   LOG.error(FileMD5Util.class.getName()+"初始化失败，MessageDigest不支持MD5Util。");
	    nsaex.printStackTrace();
	   }
	}

	public static void main(String[] args) throws Exception {
//	   long begin = System.currentTimeMillis();
//	   File big = new File("D:/SOFTS/apache-maven.rar");
//	   String md5=getFileMD5String(big);
//	   long end = System.currentTimeMillis();
//	   LOG.info("md5:"+md5+" time:"+((end-begin)/1000)+"s");
		String str = FileMD5Util.getMD5String("0;23C070CA90034BA9AFD50A39F090B037;2018-03-22 00:00:00;2018-03-23 00:00:00");
		System.out.println(str);
	}


	public static String getFileMD5String(File file) throws IOException {
	   FileInputStream in = new FileInputStream(file);
	   FileChannel ch = in.getChannel();
	   MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
	   messagedigest.update(byteBuffer);
	   String md5 = bufferToHex(messagedigest.digest());
	   in.close();
	   return md5;
	}

	public static String getMD5String(String s) {
	   return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
	   messagedigest.update(bytes);
	   return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
	   return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
	   StringBuffer stringbuffer = new StringBuffer(2 * n);
	   int k = m + n;
	   for (int l = m; l < k; l++) {
	    appendHexPair(bytes[l], stringbuffer);
	   }
	   return stringbuffer.toString();
	}


	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
	   char c0 = hexDigits[(bt & 0xf0) >> 4];
	   char c1 = hexDigits[bt & 0xf];
	   stringbuffer.append(c0);
	   stringbuffer.append(c1);
	}

	public static boolean checkPassword(String password, String md5PwdStr) {
	   String s = getMD5String(password);
	   return s.equals(md5PwdStr);
	}
	
	public static String GetSalt(){
        Random ran = new Random();
        String Salt = null;
        for (int i=0;i<4;i++){
            int count = ran.nextInt(10-1);
            Salt += SaltDigits[count];
        }
        return Salt;
    }
}
