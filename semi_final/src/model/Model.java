
package model;


import dao.ADao;
import dao.LoginDAO;
import dto.AGroup;
import dto.AJoin;
import dto.AMember;
import dto.Board;
import dto.BoardComm;
import java.util.ArrayList;



public class Model {

     // 회원가입 
    public int joinMemberMd(AMember vo) {
        return LoginDAO.getLoginDao().joinMember(vo);
    }
     //로그인
    public AMember loginMd( AMember vo,String id) {
       return LoginDAO.getLoginDao().login(vo,id);
    }
    //로그인id - pwd 체크
    public int checkIdMd(String id, String pwd){
        return LoginDAO.getLoginDao().checkId(id, pwd);
    }
    
    
    //그룹만들기
     public void addGroupMd(AMember ref , AGroup ref2){
         ADao.getDao().addGroup(ref, ref2);
     }
     // 메인페이지 그룹 리스트
      public ArrayList<AGroup> groupListMd(AMember ref) {
          return ADao.getDao().groupList(ref);
      }
       //모임 이름검색하기
    public ArrayList<AGroup> detailGroup(String gname){
        return ADao.getDao().detailGroup(gname);
    }
    //모임 가입신청
    public void joinMd(AJoin aj){
        ADao.getDao().joinDelay(aj);
    }
    // 가입 신청 여부
    public int ckJoinDelay(AJoin aj){
        return ADao.getDao().ckJoinDelay(aj);
    }
    // 모임 존재여부
    public int ckGroup(AGroup g){
        return ADao.getDao().ckGroup(g);
    }
 // 모임 가입 수락
    public void acceptJoin(int a, int b){
        ADao.getDao().acceptJoin(a,b);
    }
    // 모임 신청인 그룹번호 가져오기
    public int getAplicantInfo(AJoin aj){
        return ADao.getDao().getAplicantInfo(aj);
    }
    // 모임 가입신청 거부
    public void denyJoin(AJoin aj){
        ADao.getDao().denyJoin(aj);
    }
    // 모임 신청자 목록 보기
    public ArrayList<AMember> getApplicantList(AGroup a){
        return ADao.getDao().getApplicantList(a);
    }
    // 모임장 여부 체크하기 
    public int ckMoimjang(AJoin aj){
    return ADao.getDao().ckMoimjang(aj);
    }
    //모임 번호 입력후 버튼 클릭 후 모임페이지에 대한 
    //상세 정보를  가져오는 Dao 
    public AGroup enterGroupMd(AGroup group){
        return ADao.getDao().EnterGroup(group);
    }
    
    //모임 상세페이지에서 모임에 가입한 맴버를 표시하는 테이블
     public ArrayList<AJoin> listGroupMd(int num){
         return ADao.getDao().listGroup(num);
     }
     //모임에서 추방
       public int kickPpl(AJoin aj){
         return ADao.getDao().kickPpl(aj);
     }
     //모임 소개글 변경
     public void changeGinfo(AGroup group){
         ADao.getDao().changeGinfo(group);
     }
     // 내 정보 변경
     public void updateMyInfoMd(AMember ref, String mloc, int mhobby){
         ADao.getDao().updateMyInfo(ref, mloc, mhobby);
     }
     
     // 내가 가입한 모임 리스트 불러오기 
     public ArrayList<AJoin> listMyGroupMd(int num) {
         return ADao.getDao().listMyGroup(num);
     }
     // 게시글 입력
     public void addBoardMd(Board b,AJoin aj){
         ADao.getDao().addBoard(b,aj);
     }
     // 게시판 글 목록 출력
     public ArrayList<Board> listBoardMd(AGroup group){
         return ADao.getDao().listBoard(group);
     }
     // 게시글 상세내용 출력
     public Board getDetail(Board a, AGroup ag) {
         return ADao.getDao().getDetailBoard(a,ag);
     }
     
     //댓글 달기
       public void addCommMd(BoardComm bc,AJoin aj){
         ADao.getDao().addComment(bc,aj);
     }
     //댓글 출력
       public ArrayList<BoardComm> getDetailBoardCommMd(BoardComm bc){
           return ADao.getDao().getDetailBoardComm(bc);
       }
       // 내가 가입한 모임 탈퇴하기
     public int deleteMyMoimMd(AJoin a){
         return ADao.getDao().deleteMyMoim(a);
     }
      // 모임장이 모임 삭제하기
     public void deleteMyMoim2(int delnum){
         ADao.getDao().deleteMyMoim2(delnum);
     }
     // 모임장인지 확인
     public int ckMoimjang2(AJoin aj){
         return ADao.getDao().ckMoimjang2(aj);
     }
     // 선택한 취미검색
      public ArrayList<AGroup> selectGroupHobby(int num) {
        return  ADao.getDao().selectGroupHobby(num);
      }
      //그룹에 가입되있는 회원 번호 출력
      public int ckGJlList(AJoin aj){
          return ADao.getDao().ckGjlist(aj);
      }
      //회원가입시 중복된 아이디 체크 
       public boolean chkUsedId(String mid){
           return ADao.getDao().chkUsedId(mid);
       }
      // 회원 탈퇴
       public void dropMemberMd(AMember mem){
            ADao.getDao().dropMember(mem);
       }
}
