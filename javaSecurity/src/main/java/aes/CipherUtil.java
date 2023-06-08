package aes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.management.loading.PrivateClassLoader;

public class CipherUtil {
	private static byte[] randomKey;
	// 초기화 벡터 : 첫번 째 블럭에 값 제공
	// CBC 모드 : 블럭 암호화 시 앞 블럭의 암호문이 뒤 블럭의 암호화에 영향을 준다.
	// 패딩 방법: 마지막 블럭의 자리수를 지정된 블럭의 크기만큼 채우기 위한 방법
	private final static byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9, 0x07, 0x72, 0x6F, (byte) 0x5A,
			(byte) 0x8E, 0x12, 0x39, (byte) 0x9, 0x07, 0x72, 0x6F, (byte) 0x5A };
	static Cipher cipher; // cipher : 암호처리를 위한 객체
	static {
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // AES/CBC/PKCS5Padding => 알고리즘/블럭암호화 모드/패딩방법
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] getRandomKey(String algo) throws NoSuchAlgorithmException {
		// algo : 암호 알고리즘 이름 => AES
		// keyGen : 알고리즘에 맞는 키 생성을 위한 객체
		KeyGenerator keyGen = KeyGenerator.getInstance(algo);
		keyGen.init(128); // AES 알고리즘 키크기 : 128 ~ 196 비트 크기 가능.
		SecretKey key = keyGen.generateKey(); // keyGen 객체에 설정 된 내용으로 키 생성
		return key.getEncoded(); // byte[] 형태로 리턴
	}

	public static String encrypt(String plain) {
		// plain : 암호화를 위한 평문 데이터
		byte[] cipherMsg = new byte[1024];
		try {
			// randomKey 대칭 키 : 암호화, 복호화에 사용되는 키가 동일(복호화 땐 만들면 안 됨)
			randomKey = getRandomKey("AES");
			// AES 알고리즘에서 사용할 key 객체로 생성
			Key key = new SecretKeySpec(randomKey, "AES");
			// CBC 방식에서 사용 할 초기화 벡터 값을 설정
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			// Cipher.ENCRYPT_MODE(암호화처리) 키,IV 설정
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);// 암호화를 위한 cipher 객체
			cipherMsg = cipher.doFinal(plain.getBytes()); // 암호화 실행

		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); // 문자열로 암호문 리턴
	}

	// byte[] 데이터 => 16진수 값을 가진 문자열 형태
	private static String byteToHex(byte[] cipherMsg) {
		if (cipherMsg == null)
			return null;
		String str = ""; // 문자열로 넣고
		for (byte b : cipherMsg) {
			str += String.format("%02X", b); // 각 바이트를 2자리 16진수로 생성
		}
		return str;
	}

	// cipherMsg : :D75D35E5299F5B1055514B9FB80CBBF83...암호문
	// decrypt : 암호화 된 데이터를 평문으로 리턴
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			// randomKey : 암호화에서 사용 된 키
			Key key = new SecretKeySpec(randomKey, "AES"); // AES알고리즘에서 사용할 키 객체로 생성
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv); // CBC 모드에서 사용할 IV 설정
			// Cipher.DECRYPT_MODE : 복호화 가능
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec); // 복호화 객체 설정
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim())); // 복호화 설정

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new String(plainMsg).trim(); // byte[] 형태의 평문을 => 문자열로 변경
	}

	// hexToByte : 암호화 된 문자열을 => Byte[] 값으로 변환
	private static byte[] hexToByte(String str) {
		// srt : D75D35E5299F5B1055514B9FB80CBBF8....
		if (str == null || str.length() < 2)
			return null; // 잘못 된 데이터.문자열로 바꿀 수 없어서 return null?
		int len = str.length() / 2; // 2개의 데이터가 한 바이트
		byte[] buf = new byte[len]; // buf : 16진수로 변환한 값을 넣어라
		for (int i = 0; i < len; i++) {
			buf[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);

		}
		return buf;
	}

	public static String encrypt(String plain1, String key) {
		byte[] cipherMsg = new byte[1024];
		try { // byte[] 알고리즘
			Key genKey = new SecretKeySpec(makekey(key), "AES"); // 128비트 크기
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, genKey, paramSpec);
			cipherMsg = cipher.doFinal(plain1.getBytes()); // 암호문

		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg);
	}

	// AES 알고리즘의 키 크기 : 128비트 = > 16바이트
	// 128비트의 크기로 변경
	private static byte[] makekey(String key) {
		// key : abc1234567
		int len = key.length(); // 10자리
		char ch = 'A';
		for (int i = len; i < 16; i++) {// 16바이트로 생성
			key += ch++; // abc1234567ABCDF
		}
		return key.substring(0, 16).getBytes(); // 16바이트로 생성
	}

	public static String decrypt(String cipher1, String key) {
		byte[] plainMsg = new byte[1024];
		try {
			// randomKey : 암호화에서 사용 된 키
			Key genkey = new SecretKeySpec(makekey(key), "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, genkey, paramSpec); // 복호화 객체 설정
			plainMsg = cipher.doFinal(hexToByte(cipher1.trim())); // hexToByte : 원래 문자열화 배열을 바이트형 배열로 바꿧 복호화 설정?

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();

	}

	public static String makehash(String msg) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] plain = msg.getBytes();
		byte[] hsah = md.digest(plain);
		return byteToHex(hsah);
	}

	public static void encryptFile(String plainFile, String cipherFile, String strkey) {
		//plainFile : 입력 파일. 평문 파일. 암호화할 파일
		//cipherFile : 출력파일. 암호문 파일
		//strkey : 키 값. abc1234567
		try {
			getKey(strkey); //key.ser 파일에 키 객체가 저장
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key) ois.readObject(); //key.ser 파일에 등록 된 key객체를 읽기
			ois.close();
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key,paramSpec); //암호화 객체 초기화
			FileInputStream fis = new FileInputStream(plainFile); //평문 파일
			FileOutputStream fos = new FileOutputStream(cipherFile); //암호문 파일
			//cipherOutputStream :cipher 객체에 설정 된 내용(암호화함)으로 출력하는 스트림
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) != -1) {
				cos.write(buf, 0, len); //cipher 객체에 설정 된 내용으로 암호화하여 fos 스트림에 출력
			}
			fis.close();
			cos.flush();
			fos.flush();
			cos.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//입력 된 키 값을 가진  key 객체를 파일(key.ser 이름)에 생성.
	private static void getKey(String key) throws Exception {
		//makekey(key) : 128비트의 키로 생성
		Key genKey = new SecretKeySpec(makekey(key), "AES");
		//key.ser이름의 파일 생성
		//ObjectOutputStream : 객체를 외부로 전송 할 수 있는 스트림
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("key.ser"));
		out.writeObject(genKey); //key.ser 파일에 키 객체 저장
		out.flush(); out.close();
	}

	public static void decryptFile(String cipherFile, String plainFile) {
		//cipherFile : 암호화 된 데이터를 저장하고 있는 암호화 파일 이름
		//plainFile : 복호화된 데이터를 저정할 출력 파일 이름. 평문 파일
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key) ois.readObject(); //key.ser 파일에서 키 객체 읽기
			ois.close();
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, key,paramSpec); //암호화 객체 초기화 : DECRYPT_MODE(복호화기능)
			FileInputStream fis = new FileInputStream(cipherFile); //평문 파일
			FileOutputStream fos = new FileOutputStream(plainFile);
			//cipher 객체에 설정 된 내용(복호화기능)으로 fos 스트림에 출력
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while((len = fis.read(buf)) != -1){
				cos.write(buf,0, len); //복호화 된 데이터를 fos에 출력
				
			}
			fis.close(); cos.flush();
			fos.flush(); cos.close(); fos.close();
			
			
		}catch (Exception e) {
		e.printStackTrace();
		}
	
	}

}