package aes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/*
 * usersecurity 테이블의 내용을 출력하기
 *   이메일은 복호화 해서 출력하기
 *   1. 암호화 키와 동일하게 처리
 *   
 * 
 */
public class CipherMain4 {

	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gdudb","gdu","1234");
		PreparedStatement pstmt = conn.prepareStatement
				       ("select userid,username,email,phoneno,birthday from usersecurity");
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		for(int i=1; i<=rsmd.getColumnCount(); i++) {
			System.out.print(rsmd.getColumnClassName(i)+"\t");
		}
		System.out.println();
		
		while(rs.next()) {
			String email = rs.getString("email");
			String userid = rs.getString("userid");
			String key = CipherUtil.makehash(userid);//hash코드
			String plainEmail = CipherUtil.decrypt(email,key);
			for(int i=1; i<= rsmd.getColumnCount();i++) {
				if(i==3)System.out.println(plainEmail +"\t"); //3인 경우엔 복구화 된 이메일 출력하고 나머지는 원래 대로 출력해라
				else System.out.print(rs.getString(i) +"\t");
			}
			System.out.println();
//			pstmt = conn.prepareStatement("update userseurity set email=? where userdid=?");
//			pstmt.setString(1, cipherEmail);
//			pstmt.setString(2, userid);
//			pstmt.executeQuery();
		}
	
	}

}
