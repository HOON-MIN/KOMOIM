/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import conn.TestConn;
import dto.AGroup;
import dto.AMember;
import dto.Hobby;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author kosmo
 */
public class GroupDAO {
    private static GroupDAO groupDao;
    public GroupDAO(){    
    }
    
    public static GroupDAO getGroupDAO(){
        if(groupDao == null){
            groupDao = new GroupDAO();
        }
        return groupDao;
    }
    
    //모임생성하기
    public void addGroup(AMember ref, AGroup ref2) {
        Connection con = null;
        PreparedStatement ps1 = null; 
        PreparedStatement ps2 = null; 
        PreparedStatement ps3 = null;

        //모임만들기
        String path = "insert into Agroup values(groupnum_seq.nextVal,?,?,?,?,sysdate)";
        String path2 = "insert into Ajoin values(?,groupnum_seq.currVal,sysdate,1)";
        try {
            con = TestConn.getConn();
            con.setAutoCommit(false);
            ps1 = con.prepareStatement(path);
            
            ps1.setString(1, ref2.getGname());
            ps1.setInt(2, ref.getMhobby());
            ps1.setString(3, ref.getMloc());
            ps1.setString(4, ref2.getGinfo());
            ps1.executeUpdate();
            ps2=con.prepareStatement(path2);
            ps2.setInt(1, ref.getMembernum());
            ps2.executeUpdate();
            System.out.println("그룹 생성 완료!");
            con.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ps2 != null) {
                    ps2.close();
                }
                if (ps1 != null) {
                    ps1.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
  
    //메인페이지 모임 리스트
    public ArrayList<AGroup> groupList(AMember ref) {
        ArrayList<AGroup> alist = new ArrayList<>();
        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            con = TestConn.getConn();
            String sql = "begin glist(?,?); end; ";
            cs = con.prepareCall(sql);
            cs.setInt(1, ref.getMhobby());
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            while(rs.next()){
                AGroup a = new AGroup();
                Hobby h = new Hobby();
                a.setGroupnum(rs.getInt("groupnum"));
                a.setGdate(rs.getNString("gdate"));
                a.setGhobby(rs.getInt("ghobby"));
                a.setGinfo(rs.getString("ginfo"));
                a.setGname(rs.getNString("gname"));
                a.setGloc(rs.getString("gloc"));
                h.setHname(rs.getString("hname"));
                a.setHobby(h);
                alist.add(a);
            }
        } catch (SQLException ex) {
           ex.printStackTrace();
        } finally{
            try {
                if(rs!=null) rs.close();
                if(cs!=null) cs.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
               ex.printStackTrace();
            }
        }

        return alist;
    }
    
       //모임 번호 입력후 버튼 클릭 후 모임페이지에 대한 
       //상세 정보를  가져오는 Dao 
       public AGroup EnterGroup(AGroup a) {
            
            Connection con = null;
            PreparedStatement ps = null; 
            ResultSet rs = null;
       try {
            con = TestConn.getConn();
            String sql = "select groupnum,gname,ginfo from agroup where groupnum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, a.getGroupnum());
            rs = ps.executeQuery();
           
            if(rs.next()){
            a.setGroupnum(rs.getInt("groupnum"));
            a.setGname(rs.getString("gname"));
            a.setGinfo(rs.getString("ginfo"));
            }
                
        } catch (SQLException ex) {
          ex.printStackTrace();
        }finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                 ex.printStackTrace();
            }
        }
        return a;

        }
    
}
