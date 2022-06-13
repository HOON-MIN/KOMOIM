package dao;
//병렬
import conn.TestConn;
import dto.AGroup;

import dto.AJoin;
import dto.AMember;
import dto.Board;
import dto.BoardComm;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import oracle.jdbc.OracleTypes;

public class ADao {

    private static ADao dao;

    private ADao() {
    }

    public static ADao getDao() {
        if (dao == null) {
            dao = new ADao();
        }
        return dao;
    }

   
    
    

         
    
    //모임검색하기
    public ArrayList<AGroup> detailGroup(String gname){
        
        ArrayList<AGroup> alist = new ArrayList<AGroup>();
        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        String path = "begin gsearch(?,?); end;";
        
        try {
            con = TestConn.getConn();
            cs = con.prepareCall(path);
            cs.setString(1, gname);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            while(rs.next()){
             AGroup a = new AGroup();
             Hobby b = new Hobby();
                a.setGname(rs.getString("gname"));
                a.setGhobby(rs.getInt("ghobby"));
                a.setGloc(rs.getString("gloc"));
                a.setGinfo(rs.getString("ginfo"));
                a.setGroupnum(rs.getInt("groupnum"));
                b.setHname(rs.getString("hname"));
                a.setHobby(b);
                alist.add(a);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            try{
                if(rs != null)rs.close();
                if(cs != null)cs.close();
                if(con != null)con.close();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
     return alist;
       
    }

     
        // 모임내 가입한 멤버 내역
        public ArrayList<AJoin> listGroup(int num) {
           ArrayList<AJoin> alist = new ArrayList<>();
            
            Connection con = null;
            CallableStatement cs = null;
            ResultSet rs = null;
            String sql = "begin p1(?,?); end;";
        try {
            con = TestConn.getConn();
            cs = con.prepareCall(sql);
            cs.setInt(1, num);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            while(rs.next()){
                AJoin ajoin = new AJoin();
                ajoin.setGroupnum(rs.getInt("groupnum"));
                ajoin.setMembernum(rs.getInt("membernum"));
                ajoin.setJdate(rs.getString("jdate"));
                
                AMember member = new AMember();
                member.setMname(rs.getString("mname"));
                member.setMgender(rs.getString("gender"));
                
                AGroup group = new AGroup();
                group.setGname(rs.getString("gname"));
                
                ajoin.setAmember(member);
                ajoin.setAgroup(group);
                alist.add(ajoin);
                
                //j.groupnum,m.mname,g.gname
            }
        } catch (SQLException ex) {
            Logger.getLogger(ADao.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                if(rs!=null) rs.close();
                if(cs!=null) cs.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                Logger.getLogger(ADao.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
            return alist;
        } 
        // 나의 정보 수정(지역 및 취미)
        public void updateMyInfo(AMember ref, String mloc, int mhobby){
            Connection con = null;
            PreparedStatement ps = null; 
            CallableStatement cs = null;
            ResultSet rs = null;
        try {
            con = TestConn.getConn();
            con.setAutoCommit(false);
            String sql = "update amember set mloc = ?, mhobby = ? where mid = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, mloc);
            ps.setInt(2, mhobby);
            ps.setString(3, ref.getMid());
            ps.executeUpdate();
            String sql2 = "begin userprofile(?,?); end;";
            cs= con.prepareCall(sql2);
            cs.setString(1, ref.getMid());
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            if(rs.next()){
            Hobby h = new Hobby();
            h.setHname(rs.getString("hname"));
            ref.setJoindate(rs.getString("fj"));
            ref.setHobby(h);
            }
            // 현재 세션의 로그인 정보도 수정

            ref.setMloc(mloc);
            ref.setMhobby(mhobby);
            con.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            try {
                if(rs!=null) rs.close();
                if(cs!=null) cs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
            
        }
        // 내가 가입한 모임 리스트 가져오기
        public ArrayList<AJoin> listMyGroup(int num) {
           ArrayList<AJoin> alist = new ArrayList<>();
            
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql = "select g.groupnum,g.gname, g.gloc, g.ghobby, j.jdate,h.hname from agroup g, ajoin j,hobby h where g.groupnum = j.groupnum and g.ghobby = h.hobbynum and j.membernum =?";
        try {
            con = TestConn.getConn();
            ps = con.prepareStatement(sql);
            ps.setInt(1, num);
            rs = ps.executeQuery();
            
            while(rs.next()){
                AJoin ajoin = new AJoin();
                
                ajoin.setJdate(rs.getString("jdate"));

                AGroup group = new AGroup();
                group.setGroupnum(rs.getInt("groupnum"));
                group.setGname(rs.getString("gname"));
                group.setGloc(rs.getString("gloc"));
                group.setGhobby(rs.getInt("ghobby"));
                Hobby h = new Hobby();
                h.setHname(rs.getString("hname"));
                group.setHobby(h);
                ajoin.setAgroup(group);
                alist.add(ajoin);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
            return alist;
        } 

        // 게시글 작성 -병렬
        public void addBoard(Board b,AJoin aj){
            Connection con = null;
            PreparedStatement ps = null;
            String sql = "insert into board values(board_seq.nextVal,?,?,?,?,sysdate,getWlist(?))";
        try {
            con = TestConn.getConn();
            ps = con.prepareStatement(sql);
            ps.setInt(1, aj.getAmember().getMembernum());
            ps.setString(2, b.getSubject());
            ps.setString(3, b.getContent());
            ps.setInt(4, aj.getAgroup().getGroupnum() );
            ps.setInt(5, aj.getAgroup().getGroupnum());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if(ps != null)  ps.close();
                if(con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
                   
        }
        // 게시글 출력 - 병렬
        public ArrayList<Board> listBoard(AGroup group){
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql ="select b.wlist, b.subject, a.mname, b.bdate from board b, amember a where a.membernum = b.membernum and b.groupnum = ? ";
            ArrayList<Board> arlist = new ArrayList<>();           
        try {
            con = TestConn.getConn();
            ps = con.prepareStatement(sql);
            ps.setInt(1, group.getGroupnum());
            rs = ps.executeQuery();       
            while(rs.next()){
                Board b = new Board();            
                b.setBoardnum(rs.getInt("wlist"));
                b.setSubject(rs.getString("subject"));
                b.setBdate(rs.getString("bdate"));
                AMember a = new AMember();
                a.setMname(rs.getString("mname"));
                b.setAmember(a);
                arlist.add(b);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if(rs != null) rs.close();
                if(ps != null)  ps.close();
                if(con != null) con.close();
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
        return arlist;
        }
    // 상세 게시글 보기
    public Board getDetailBoard(Board a,AGroup ag) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql ="select b.boardnum,b.wlist,b.subject,a.mname,b.content from board b, amember a where b.membernum = a.membernum and b.wlist = ? and groupnum =?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, a.getWlist());
            ps.setInt(2, ag.getGroupnum());
            rs = ps.executeQuery();
           
            if (rs.next()) {
                a.setBoardnum(rs.getInt("boardnum"));
                a.setWlist(rs.getInt("wlist"));
                a.setSubject(rs.getString("subject"));
                AMember b = new AMember();
                b.setMname(rs.getString("mname"));
                a.setContent(rs.getString("content"));
                a.setAmember(b);
            }
            System.out.println("로그인 성공");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (rs != null){
                    rs.close();
                }
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
        return a;
    } 
    //댓글출력하기 - 김훈민
     public ArrayList<BoardComm> getDetailBoardComm(BoardComm bc) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<BoardComm> alist = new ArrayList<>();
        try {
            con = TestConn.getConn();
            String sql ="select a.mname,bc.content ,bc.cdate from amember a , boardcomm bc where bc.membernum = a.membernum and bc.boardnum = ? order by cdate asc";
            ps = con.prepareStatement(sql);
            ps.setInt(1, bc.getBo().getBoardnum());
            rs = ps.executeQuery();
           
            while(rs.next()) {
                bc = new BoardComm();
                AMember b = new AMember();
                b.setMname(rs.getString("mname"));
                bc.setContent(rs.getString("content"));
                bc.setCdate(rs.getString("cdate"));
                bc.setAmember(b);
                alist.add(bc);
                System.out.println(bc.getContent());
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null){rs.close();}
                if (ps != null){ps.close();}
                if (con != null){con.close();}
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return alist;
    } 
    
    
    
    //댓글내용저장하기 - 김훈민
      public void addComment(BoardComm bc,AJoin aj){
            Connection con = null;
            PreparedStatement ps = null;
            String sql = "insert into boardcomm values(boardcomm_seq.nextVal,?,?,?,sysdate)";
        try {
            con = TestConn.getConn();
            ps = con.prepareStatement(sql);
            ps.setInt(1, bc.getBo().getBoardnum());
            ps.setInt(2, aj.getAmember().getMembernum());
            ps.setString(3, bc.getContent());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if(ps != null)  ps.close();
                if(con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
      }  
      // 병렬
      public int deleteMyMoim(AJoin a){
            //내가 가입한 모임을 탈퇴
            Connection con = null;
            PreparedStatement ps =null;
            CallableStatement cs = null;
            ResultSet rs = null;
        try {
            int ck = 0;
            con = TestConn.getConn();
            con.setAutoCommit(false);
            
            String sql = "select groupnum from ajoin where membernum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, a.getMembernum());
            
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("groupnum") == a.getGroupnum()){                 
                     ck = 1;     
                }
            }
            if(ck==1){
            String sql2 = "begin delmoim(?,?); end;";
            cs = con.prepareCall(sql2);
            cs.setInt(1, a.getMembernum());
            cs.setInt(2, a.getGroupnum());
            cs.executeUpdate();
            con.commit();
            return 1;
            }   
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            try {
                if(cs!=null) cs.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return 0;  
        }
       public void deleteMyMoim2(int delnum){
            // 모임 삭제
            Connection con = null;
            CallableStatement cs = null;
        try {
            con = TestConn.getConn();
            String sql = "begin dropMoim(?); end;";
            cs = con.prepareCall(sql);
            cs.setInt(1, delnum);   
            cs.execute();           
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            try {
                if(cs!=null) cs.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
            
        }
      public ArrayList<AGroup> selectGroupHobby(int num) {
            //검색한 취미를 관심사로 가지는 모임리스트 가져오기
            ArrayList<AGroup> alist = new ArrayList<>();
            
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql = "select g.groupnum, g.gname, g.ghobby, g.gloc,g.ginfo, h.hname from agroup g,hobby h  where  g.ghobby = h.hobbynum and ghobby = ?";
        try {
            con = TestConn.getConn();
            ps = con.prepareStatement(sql);
            ps.setInt(1, num);
            rs = ps.executeQuery();
            
            while(rs.next()){
                AGroup group = new AGroup();
                Hobby h = new Hobby();
                group.setGroupnum(rs.getInt("groupnum"));
                group.setGname(rs.getString("gname"));
                group.setGinfo(rs.getString("ginfo"));
                group.setGhobby(rs.getInt("ghobby"));
                group.setGloc(rs.getString("gloc"));
                h.setHname(rs.getString("hname"));
                group.setHobby(h);
                alist.add(group);
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
            return alist;
        }
      
      public int ckGjlist(AJoin aj){
      
          Connection con = null;
          CallableStatement cs = null;
          ResultSet rs = null;
          String sql = "begin ckgjlist(?,?); end; ";
        try {
            con = TestConn.getConn();
            cs = con.prepareCall(sql);
            cs.setInt(1, aj.getAgroup().getGroupnum());
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs=(ResultSet) cs.getObject(2);
            while(rs.next()){
                if(rs.getInt("membernum")==aj.getAmember().getMembernum()){
                    return 1;
                }
            
            }
        } catch (SQLException ex) {
            Logger.getLogger(ADao.class.getName()).log(Level.SEVERE, null, ex);
        }
          return 0;
      }
      
      // 가입 신청했는지 여부
      public int ckJoinDelay(AJoin aj){
        Connection con = null;
        CallableStatement cs = null, cs2 =null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "begin ckJoinDelay(?,?); end;";
            cs = con.prepareCall(sql);
            cs.setInt(1, aj.getAgroup().getGroupnum());
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            rs = (ResultSet) cs.getObject(2);
            while(rs.next()){
                if(rs.getInt("membernum")==aj.getAmember().getMembernum()){
                return 0;
                }
            }
            String sql2 = "begin ckJoinDelay2(?,?); end;";      
            cs2 = con.prepareCall(sql2);
            cs2.setInt(1, aj.getAgroup().getGroupnum());
            cs2.registerOutParameter(2, OracleTypes.CURSOR);
            cs2.execute();
            rs = (ResultSet) cs2.getObject(2);
            while(rs.next()){
                if(rs.getInt("membernum")==aj.getAmember().getMembernum()){
                return -1;
                }    
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(rs!=null) rs.close();
                if(cs!=null) cs.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }          
         return 1;
      }
      //가입신청
      public void joinDelay(AJoin aj){
          Connection con = null;
          CallableStatement cs = null;
          String sql= "begin join_pkg.joindelay(?,?); end;";
        try {
            con = TestConn.getConn();
            cs = con.prepareCall(sql);
            cs.setInt(1, aj.getAmember().getMembernum());
            cs.setInt(2, aj.getAgroup().getGroupnum());
            cs.execute();
        } catch (SQLException ex) {
              try {
                  con.rollback();
              } catch (SQLException ex1) {
                  ex1.printStackTrace();
              }
            ex.printStackTrace();
        } finally{
            try{
                if(cs != null)cs.close();
                if(con != null)con.close();
           }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
      
          //가입수락  
     public void acceptJoin(int membernum, int groupnum) { // loginUserInfo - iuinInfo // contected Group - conGroup
        Connection con = null;
        CallableStatement cs = null;
        String path = "begin join_pkg.accetjoin(?,?); end; ";
        try {
            con = TestConn.getConn(); 
            cs = con.prepareCall(path);
            cs.setInt(1, membernum);
            cs.setInt(2, groupnum);
            cs.executeUpdate();
        } catch (SQLException ex) {
           try {
                 ex.printStackTrace();
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ADao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }finally{
            try{
                if(cs != null)cs.close();
                if(con != null)con.close();
           }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    // 신청자 정보 얻어오기
    public AJoin getAplicantInfo(int mnum){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        AJoin aj = new AJoin();
        try {
            con = TestConn.getConn();
            String sql = "select membernum, groupnum from ajoin_delay where membernum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, mnum);
            rs = ps.executeQuery();
            if(rs.next()){
            AMember mem = new AMember();
            AGroup a = new AGroup();
            mem.setMembernum(rs.getInt("membernum"));
            a.setGroupnum(rs.getInt("groupnum"));
            aj.setAmember(mem);
            aj.setAgroup(a);
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
        return aj;
    }
    //그룹 존재여부
    public int ckGroup(AGroup g){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select groupnum from gview";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("groupnum")==g.getGroupnum()){
                return 1;
                }
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
        return 0;
    
    }
    
    //모임 신청 거절
    public void denyJoin(AJoin aj){
        Connection con = null;
        CallableStatement cs = null;
        String path = "begin join_pkg.denyjoin(?,?); end; ";
        try {
            con = TestConn.getConn(); 
            cs = con.prepareCall(path);
            cs.setInt(1, aj.getAmember().getMembernum());
            cs.setInt(2, aj.getAgroup().getGroupnum());
            cs.executeUpdate();
        } catch (SQLException ex) {
           try {
                 ex.printStackTrace();
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ADao.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }finally{
            try{
                if(cs != null)cs.close();
                if(con != null)con.close();
           }catch(SQLException ex){
                ex.printStackTrace();
            }
        }    
    }
    // 모임장 여부 체크
    public int ckMoimjang(AJoin aj){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select membernum from ajoin where joinmember =1 and groupnum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, aj.getAgroup().getGroupnum());
            rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getInt("membernum")==aj.getAmember().getMembernum()){
                return 1;
                }
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
    return 0;
    }
    
    // 신청자 목록
    
    public ArrayList<AMember> getApplicantList(AGroup a){
        ArrayList<AMember> alist = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select m.membernum,m.mid,m.mname,getage(m.mjumin) age,m.gender,m.mloc from amember m, ajoin_delay d where m.membernum = d.membernum and d.groupnum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, a.getGroupnum());
            rs = ps.executeQuery();
            while(rs.next()){
            AMember mem = new AMember();
            mem.setMembernum(rs.getInt("membernum"));
            mem.setMid(rs.getString("mid"));
            mem.setMname(rs.getString("mname"));
            mem.setAge(rs.getInt("age"));
            mem.setMgender(rs.getString("gender"));
            mem.setMloc(rs.getString("mloc"));
            alist.add(mem);
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
        
        return alist;
    }
      // 모임에서 추방
    public int kickPpl(AJoin aj){
        Connection con = null;
        PreparedStatement ps = null, ps2 =null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select membernum from ajoin where groupnum = ? and joinmember = 1";
            ps = con.prepareStatement(sql);
            ps.setInt(1, aj.getAgroup().getGroupnum());
            rs = ps.executeQuery();
            int a = 0;
            while(rs.next()){
                if(rs.getInt("membernum") == aj.getAmember().getMembernum()){
                    a=1;
                }
            }
            if(a==0){
            String sql2 = "delete from ajoin where groupnum = ? and membernum = ?";
            ps2 = con.prepareStatement(sql2);
            ps2.setInt(1, aj.getAgroup().getGroupnum());
            ps2.setInt(2, aj.getAmember().getMembernum());
            ps2.executeUpdate();
            return 1;
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }

        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }
    // 모임장 확인
    public int ckMoimjang2(AJoin aj){
        Connection con = null;
        PreparedStatement ps = null, ps2 =null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select membernum from ajoin where groupnum = ? and joinmember = 1";
            ps = con.prepareStatement(sql);
            ps.setInt(1, aj.getGroupnum());
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("membernum") == aj.getMembernum()){
                    return 1;
                }
            }
    }catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }

        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }
    // 모임 소개글 변경
    public void changeGinfo(AGroup g){
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = TestConn.getConn();
            String sql = "update agroup set ginfo = ? where groupnum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(2, g.getGroupnum());
            ps.setString(1, g.getGinfo());
            ps.executeUpdate();
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
    }    
   public boolean chkUsedId(String mid){
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select count(mid) cnt from amember where mid = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, mid);
            rs = ps.executeQuery();
             
             if (rs.next()) {
                 System.out.println(rs.getInt("cnt"));
                int cnt = rs.getInt("cnt");
                if(cnt > 0){
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
                try {
                if (rs != null) {
                    rs.close();
                }
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
        return false;
    }
   public int getAplicantInfo(AJoin aj){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = TestConn.getConn();
            String sql = "select membernum from ajoin_delay where groupnum = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, aj.getAgroup().getGroupnum());
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("membernum")==aj.getAmember().getMembernum())
                    return 1;
            }
        } catch (SQLException ex) {
            try {
                con.rollback();
                ex.printStackTrace();
            } catch (SQLException ex1) {
                 ex1.printStackTrace();
            }
            
        } finally{
            try {
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
        return 0;
    }
   public void dropMember(AMember mem){
            // 회원 탈퇴
            Connection con = null;
            CallableStatement cs = null; 
        try {
            con = TestConn.getConn();
            String sql = "begin dropMember(?); end;";
            cs = con.prepareCall(sql);
            cs.setInt(1, mem.getMembernum());
            cs.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            try {
                if(cs!=null) cs.close();
                if(con!=null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

    

