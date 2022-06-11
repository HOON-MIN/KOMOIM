package dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import conn.GetConn;
import dto.Chat;

public class Adao {

	// 싱글톤 : 하나의 클래스만 존재
	private static Adao dao;
	private Adao() {}
	public static Adao getDao() {
		if(dao == null) {
			dao= new Adao();			
		}
		return dao;
	}
	// Dao에서 JDBC로 채팅 내용을 보내줌
	public void addChat(Chat c) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql= "insert into chat values(chat_seq.nextVal,?,?,sysdate)";
		try {
			con = GetConn.getConn();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, c.getGroupnum());
			pstmt.setString(2, c.getTalk());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				if(pstmt!=null)pstmt.close();
				if(con!=null)con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
