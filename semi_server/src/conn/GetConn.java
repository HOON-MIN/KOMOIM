package conn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GetConn {

	// static{} 메인 시작전에 Class.forName을 통해 해당 드라이버 로딩  
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("Driver On!");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	// drivermanager를 통해 커넥션을 반환하는 메서드
	public static Connection getConn() throws SQLException {
		// properties 파일을 통해 외부에서 쉽게 유지 보수가능
		Properties prop = new Properties();
		String url = "";
		String user = "";
		String pwd = "";
		try(FileReader fr = new FileReader("src/conn/UserInfo.properties");) {
			prop.load(fr);
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			pwd = prop.getProperty("pwd");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return DriverManager.getConnection(url, user, pwd);
	}
	
}
