package aes;

public class CipherMain5 {
	public static void main(String[] args) {
		String key = "abc1234567";
		//p1.txt : 평문 텍스트 파일 이름
		//c.sec : 암호문 파일 이름
		CipherUtil.encryptFile("p1.txt","c.sec",key);  //encryptFile에서 p1.txt를 읽어서 c.sec으로 암호화하겠다? 
		// 실행시 c.sec, ket.ser 파일 생성 됨 

}
}