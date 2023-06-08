package aes;
/*
 * 키를 설정하여 AES 암호화 하기
 */

public class CipherMain2 {
	public static void main(String[] args) {
	    String plain1 = "안녕하세요 홍길동 입니다.";
	    String key = "abc1234567"; //암호화의 키와 복호화의 키가 같아야 된다.
	    String cipher1 = CipherUtil.encrypt(plain1,key); //암호문  encrypt: 암호화를 해준다.
	    //암호문을 출력. 실행시 마다 달라짐 key가 계속 변경 됨
	    
	    // String key = "abc123456"; 이런 식으로 키 값이 다르면 안 됨
	    
	    String plain2 = CipherUtil.decrypt(cipher1,key); //복호화
	    System.out.println("복호문:"+plain2); //plain1 == plain2
	    
		

	}

}
