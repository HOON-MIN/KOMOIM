package conn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GetConn {

	// static{} ���� �������� Class.forName�� ���� �ش� ����̹� �ε�  
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("Driver On!");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	// drivermanager�� ���� Ŀ�ؼ��� ��ȯ�ϴ� �޼���
	public static Connection getConn() throws SQLException {
		// properties ������ ���� �ܺο��� ���� ���� ��������
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
