package rsa;

import javax.print.DocFlavor.STRING;

/*
 * 공개키 암호화 예제
 *  RSA 알고리즘 이용 => 개인키,공개키를 이용하여 암호화, 복호화에서 사용됨.
 *     개인키 암호화 => 공개키로 복구화(개인키롤 암호화를 하면 공개키로 복구화를 해야됨) -> 부인 방지.
 *     공개키 암호화 => 개인키로 복구화(공개키롤 암호화를 하면 개인키로 복구화를 해야됨) -> 기밀 문서.
 */
public class CipherMain1 {
	public static void main(String[] args) {
		String plain1 = "안녕하세요.홍길동 입니다.";
		String cipher1 = CipherRSA.encrypt(plain1);
		System.out.println("암호문:" + cipher1);
		String plain2 = CipherRSA.decrypt(cipher1);
		System.out.println("복호문:" + plain2);
		


	}

}
