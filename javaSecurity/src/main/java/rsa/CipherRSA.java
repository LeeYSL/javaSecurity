package rsa;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Stack;

import javax.crypto.Cipher;

public class CipherRSA {
   static Cipher cipher;
   static PrivateKey priKey;
   static PublicKey pubkey;
   static {
	   try {
		   cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //RSA 알고리즘
		   //공개키(RSA) 암호화에 사용 될 키 생성
		   KeyPairGenerator key = KeyPairGenerator.getInstance("RSA"); //RSA는 키가 두 개 필요하다? 
		   KeyPair KeyPair = key.genKeyPair(); //키의 쌍인 객체 저장
		   priKey = KeyPair.getPrivate(); //개인키 저장
		   pubkey = KeyPair.getPublic();  //공개키 저장
		   
	   }catch (Exception e) {
		   e.printStackTrace();
	}
   }
   
	public static String encrypt(String plain1) { //암호화
		//plain1 : 평문
		byte[] cipherMsg = new byte[1024];
	    try {
	    	cipher.init(Cipher.ENCRYPT_MODE,priKey); //암호화 모드,개인키
	    	cipherMsg = cipher.doFinal(plain1.getBytes()); //암호화 진행
	    	
	    }catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg); //16진수 문자열로 리턴
	}

	private static String byteToHex(byte[] cipherMsg) {
		 if(cipherMsg == null ) 
		return null;
		 String str = "";
		 for(byte b : cipherMsg) { 
			 str += String.format("%02X", b);
			 
		 }
		 return str;
	}
	public static String decrypt(String cipher1) {
		//cipher1 : 암호문.16진수 표현 된 문자열
		byte[] plainMsg = new byte[1024];
		try {
			cipher.init(Cipher.DECRYPT_MODE, pubkey);
			plainMsg = cipher.doFinal(hexToByte(cipher1.trim()));
		}catch (Exception e) {
	    	e.printStackTrace();
		}
	  return new String(plainMsg).trim();
	}
	//16진수 표현의 문자열 => byte[]
     private static byte[] hexToByte(String str) {
    	 if(str == null || str.length() < 2) return null;
    	 byte[] buf = new byte[str.length()/2];
    	 for(int i=0; i<buf.length;i++) {
    		 buf[i] = (byte)Integer.parseInt(str.substring(i*2,i*2+2),16);
    	 }
    	 return buf;
     }

	public static void getKey() {
	   try {
		   KeyPairGenerator key = KeyPairGenerator.getInstance("RSA");
		   key.initialize(2048); //2048 비트 키 생성
		   KeyPair keyPair = key.generateKeyPair();
		   PrivateKey priKey = keyPair.getPrivate(); //개인키
		   PublicKey pubKey = keyPair.getPublic(); //공개키
		   ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("privateKey.ser"));
		   out.writeObject(priKey); //개인키를 파일로 저장
		   out.flush();out.close();
		   out = new ObjectOutputStream(new FileOutputStream("publicKey.ser"));
		   out.writeObject(pubKey); //공개키를 파일로 저장
		   out.flush();out.close();
	   } catch (Exception e) {
		e.printStackTrace();
	}
	   
	}
	   
		public static PublicKey getPublicKey() {
			ObjectInputStream ois = null;
			PublicKey pubkey = null;
			try {
				ois = new ObjectInputStream(new FileInputStream("publickey.ser"));
				pubkey = (PublicKey)ois.readObject();
				ois.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		   return pubkey;	
	
		}
		public static PrivateKey getPrivateKey() { //개인키 읽기
			ObjectInputStream ois = null;
			PrivateKey priKey = null;
			try{
				ois = new ObjectInputStream(new FileInputStream("private.ser"));
				priKey = (PrivateKey)ois.readObject();
				ois.close();
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return priKey;
		}
		public static String encrypt(String org, int menu1) { //암호화 
			byte[] cipherMsg = new byte[1024];
			try {
				if(menu1==1) cipher.init(Cipher.ENCRYPT_MODE,getPublicKey()); //기밀문서 : 공개키로 암호화
				else cipher.init(Cipher.ENCRYPT_MODE,getPrivateKey()); //본인문서인증 : 개인키로 암호화
				cipherMsg = cipher.doFinal(org.getBytes()); 
			}catch (Exception e) {
				e.printStackTrace();
			}
			return byteToHex(cipherMsg);
		}
		public static String decrypt(String cipherMsg, int menu1) { //복호화
			byte[] plainMsg = new byte[1024];
			try {
				if(menu1==1) cipher.init(Cipher.DECRYPT_MODE,getPublicKey()); //기밀문서 : 개인키로 복호화
				else cipher.init(Cipher.DECRYPT_MODE,getPrivateKey()); //본인인증 문서 : 공개키로 복호화
		}catch (Exception e) {
			e.printStackTrace();
		}
			return new String(plainMsg).trim();
	}

	
	
}
 