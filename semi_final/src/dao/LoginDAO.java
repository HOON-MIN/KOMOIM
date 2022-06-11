/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import conn.TestConn;
import dto.AMember;
import dto.Hobby;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author kosmo
 */
public class LoginDAO {
    private static LoginDAO loginDao;
    
    public LoginDAO(){
    }
   
    public static LoginDAO getLoginDao() {
        if (loginDao == null) {
            loginDao = new LoginDAO();
        }
        return loginDao;
    }
    
    
    // 회원가입 
    public int joinMember(AMember vo) {
        Connection con = null;
        PreparedStatement ps1 = null, ps2 = null;
        ResultSet rs = null;
        
        int i = 0;
        try {
            con = TestConn.getConn();
            
            String res = "select mid from amember where mid = ?";
            ps1 = con.prepareStatement(res);
            ps1.setString(1, vo.getMid());
            rs = ps1.executeQuery();
            while(rs.next()){
                if(rs.getString("mid").equals(vo.getMid())){
                    i = 1;
                }
            }
            if(i == 0){
            String query = "insert into AMember values(membernum_seq.nextVal,?,?,?,?,?,?,sysdate,usergender(?))";
            ps2 = con.prepareStatement(query);
            ps2.setString(1, vo.getMid());
            ps2.setString(2, vo.getMpwd());
            ps2.setString(3, vo.getMname());
            ps2.setString(4, vo.getMloc());
            ps2.setInt(5, vo.getMhobby());
            ps2.setString(6, vo.getMjumin());
            ps2.setString(7, vo.getMjumin());
            ps2.executeUpdate();
            System.out.println("가입 완료!");
            
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
           
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return i;
    }
    //로그인
    public AMember login(AMember vo, String id) {

        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        String file = "src/dto/signup.txt";
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, true));) {
            con = TestConn.getConn();
            String query = "begin userprofile(?,?); end;";
            cs = con.prepareCall(query);
            cs.setString(1, id);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            if (rs.next()) {
                vo.setMembernum(rs.getInt("membernum"));
                vo.setMid(rs.getString("mid"));
                vo.setMpwd(rs.getString("mpwd"));
                vo.setMname(rs.getString("mname"));
                vo.setMloc(rs.getString("mloc"));
                vo.setMjumin(rs.getString("mjumin"));
                vo.setMhobby(rs.getInt("mhobby"));
                vo.setMdate(rs.getString("mdate"));
                vo.setMjumin(rs.getString("gender"));
                Hobby h = new Hobby();
                h.setHname(rs.getString("hname"));
                vo.setHobby(h);
                vo.setJoindate(rs.getString("fj"));
                dos.writeInt(vo.getMembernum());
                dos.writeUTF(vo.getMid());
                dos.writeUTF("날짜 : " + LocalDate.now().toString() + "  시간 : " + LocalTime.now().toString());
            }

            System.out.println("로그인 성공");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ADao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if(rs!=null) {rs.close();}
                if (cs != null) {
                    cs.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
               ex.printStackTrace();
            }
        }
        return vo;
    }

    //로그인id - pwd 체크
    public int checkId(String id, String pwd) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int i = 0;
        try {

            con = TestConn.getConn();
            String query = "select mpwd from AMember where mid = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString("mpwd").equals(pwd)) {

                    System.out.println("로그인 성공");
                    return 1;
                } else {
                    System.out.println("아이디와 비밀번호가 불일치합니다");
                }
                return 0;
            }
            return -1;

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return i;
    }
    
    
}
