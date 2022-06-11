package front;

import Chat.Cat;
import Chat.Dog;
import Chat.Wolf;
import dao.ADao;
import dto.AGroup;
import dto.AJoin;
import model.Model;
import dto.AMember;
import dto.Board;
import dto.BoardComm;
import dto.Hobby;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Asemi_ui_1 extends javax.swing.JFrame {
    private CardLayout card;
    private Model md;
    private AMember mem;
    private AGroup group;
    private Board board;
    private Socket s;
    private PrintWriter pw;
    private int hobbynum;
    
    public Asemi_ui_1() {
        initComponents();
        card = (CardLayout) pp.getLayout();
        md = new Model();
        loginImgRandom();
    }
    
        //로그인 화면 랜덤 이미지 show
    public void loginImgRandom(){
            new Thread(new Runnable() {
            int ck;
            @Override
            public void run() {
                try {
                    loginBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                           String id = log_id.getText();
                           String pwd = log_pwd.getText();
                           ck = md.checkIdMd(id, pwd);
                        }
                    });
                    while(ck!=1) {
                    int a = (int) (Math.random()*5)+1;
                    show_hobby.setIcon(new javax.swing.ImageIcon(getClass().getResource("/front/img/hobby"+a+".png")));
                    Thread.sleep(1500);
                     }
               } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();     
    }
    
        // 메인페이지 이미지 show
    public void mainImgShow(int num){
       String hname[] = {"아웃도어/여행", "운동/스포츠", "사진/영상", "게임/오락", "요리/제조"};
       main_img.setText("내가 선택한 취미는 "+hname[num-1]+"입니다.");
       main_img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/front/img/hobby"+num+".png")));
    }
    
    //모임장페이지 가입 신청목록
    public void getApplicantList(ArrayList<AMember> alist){
    
    String[] colums = {"회원번호","아이디","이름","나이","성별","지역"};
    Object[][] data = new Object[alist.size()][];
    int i = 0;
    for(AMember e : alist){
        data[i] =  new Object[6];
        data[i][0] = e.getMembernum();
        data[i][1] = e.getMid();
        data[i][2] =e.getMname();
        data[i][3] =e.getAge();
        data[i][4] =e.getMgender();
        data[i][5] =e.getMloc();
        i++;
    }
    MyTable m1 = new MyTable(colums, data);
    p11_joinTable.setModel(m1);
    
    }
    
    //채팅 연결
    public void chatConn(){
            
        try {
            s= new Socket("localhost",9999);
            pw = new PrintWriter(s.getOutputStream(), true);
            pw.println("welcome/"+group.getGroupnum()+"/"+mem.getMid()+"/msg");
             new Thread(new Runnable() {
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                int ck;
                @Override
                public void run() {
                    chating_pageBtn2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                          ck = 1;
                          pw.println("exit/"+group.getGroupnum()+"/"+mem.getMid()+"/msg");
                        }
                    });
                    tomoiminfoBtn2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                          ck = 1;
                          pw.println("exit/"+group.getGroupnum()+"/"+mem.getMid()+"/msg");
                        }
                    });
                    toboardpanalBtn2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                          ck = 1;
                          pw.println("exit/"+group.getGroupnum()+"/"+mem.getMid()+"/msg");
                        }
                    });

                    while(ck!=1){
                        try {
                            String msg = br.readLine();
                            System.out.println(msg);
                            chatScreen.append(msg+"\n");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();
            card.show(pp, "c7");
         } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    
    //메인페이지 그룹 리스트 jtable 메서드
    public void groupList(ArrayList<AGroup> b) {
        ArrayList<AGroup> a = b;
        String[] columnNames = {"모임번호", "이름","소개글","취미","지역"};
        Object[][] data = new Object[a.size()][];
        int i = 0;
        for(AGroup e : a){
            data[i] = new Object[5];
            data[i][0] = e.getGroupnum();
            data[i][1] = e.getGname();
            data[i][2] = e.getGinfo();
            data[i][3] = e.getHobby().getHname();
            data[i][4] = e.getGloc();
            i++;
        }
        MyTable mt = new MyTable(columnNames, data);
        mainPrintTable.setModel(mt);
    }
    //모임에 가입된 회원 모임 내 리스트 jtable 메서드
         public void groupdetailList(ArrayList<AJoin> b) {
        ArrayList<AJoin> a = b;
        String[] columnNames = {"회원번호","이름", "성별","가입일"};
        Object[][] data = new Object[a.size()][];
        int i = 0;
        for(AJoin e : a){
            data[i] = new Object[4];
            data[i][0] = e.getMembernum();
            data[i][1] = e.getAmember().getMname();
            data[i][2] = e.getAmember().getMgender();
            data[i][3] = e.getJdate();
            i++;
        }
        MyTable mt = new MyTable(columnNames, data);
        moimlist.setModel(mt);
        p11_delTable.setModel(mt);
    }
         
         // 내가 가입한 모임 리스트 표에 출력
        public void myGroupList(ArrayList<AJoin> b) {
        ArrayList<AJoin> a = b;
        String[] columnNames = {"그룹번호","모임 이름", "모임 지역", "취미 번호", "가입 일자"};
        Object[][] data = new Object[a.size()][];
        int i = 0;
        for(AJoin e : a){
            data[i] = new Object[5];
            data[i][0] = e.getAgroup().getGroupnum();            
            data[i][1] = e.getAgroup().getGname();
            data[i][2] = e.getAgroup().getGloc();
            data[i][3] = e.getAgroup().getHobby().getHname();
            data[i][4] = e.getJdate();
            i++;
        }
        MyTable mt = new MyTable(columnNames, data);
        mymoimList_table.setModel(mt);
    }
        
        // jtable - 게시판 글 목록 테이블
        public void listContent(){
            
        ArrayList<Board> list = md.listBoardMd(group);
        String[] cname = {"게시글번호","제목","작성자","날짜"};
        Object[][] data = new Object[list.size()][];
        int i =0;
        for(Board e : list){
            data[i] = new Object[4];
            data[i][0] = e.getBoardnum();
            data[i][1] = e.getSubject();
            data[i][2] = e.getAmember().getMname();
            data[i][3] = e.getBdate();
            i++;
        }
        MyTable mt = new MyTable(cname, data);
        board_Table.setModel(mt);
        }
        
        //p9 - 댓글 목록 출력
        public void printCommentlist(BoardComm bc){
            
            ArrayList<BoardComm> alist = md.getDetailBoardCommMd(bc);
            StringBuilder sb = new StringBuilder();
            String res = "";
            for (BoardComm e : alist) {
                 sb.append(e.getAmember().getMname()).append("님의 댓글 : ").append(e.getContent()).append("\n");  
                 sb.append("------------------------------------------------\n");
           }
        res = sb.toString();
        comment_area.setText(res);
        }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel7 = new javax.swing.JLabel();
        pp = new javax.swing.JPanel();
        p1_login = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        log_id = new javax.swing.JTextField();
        log_pwd = new javax.swing.JTextField();
        loginBtn = new javax.swing.JButton();
        joinBtn = new javax.swing.JButton();
        show_hobby = new javax.swing.JLabel();
        p2_adduser = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        prevBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        join_pwd = new javax.swing.JTextField();
        join_id = new javax.swing.JTextField();
        join_locCom = new javax.swing.JComboBox<>();
        join_hobbyCom = new javax.swing.JComboBox<>();
        join_name1 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jumin_front = new javax.swing.JTextField();
        jumin_behind = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        useMid = new javax.swing.JButton();
        p3_createmoim = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cmoim_name = new javax.swing.JTextField();
        cmoim_info = new javax.swing.JTextField();
        cmoimBtn = new javax.swing.JButton();
        main_page_Btn2 = new javax.swing.JButton();
        p4_mainpanal = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        searchword = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        moim_crea_Btn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        mainPrintTable = new javax.swing.JTable();
        tomymoimPanalBtn = new javax.swing.JButton();
        hobby1Btn = new javax.swing.JButton();
        hobby2Btn = new javax.swing.JButton();
        hobby3Btn = new javax.swing.JButton();
        hobby4Byn = new javax.swing.JButton();
        hobby5Btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        searchnum = new javax.swing.JTextField();
        moim_detail_Btn = new javax.swing.JButton();
        main_img = new javax.swing.JLabel();
        p5_moimpage = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        moim_join_Btn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        moim_info = new javax.swing.JTextArea();
        mainpageBtn = new javax.swing.JButton();
        moimTitle = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        moimlist = new javax.swing.JTable();
        tochatingPanalBtn = new javax.swing.JButton();
        tomoiminfoBtn = new javax.swing.JButton();
        toboardpanalBtn = new javax.swing.JButton();
        p6_boardPanal = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        board_Table = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        search_detail_board = new javax.swing.JTextField();
        detail_board_show_Btn = new javax.swing.JButton();
        create_board_Btn = new javax.swing.JButton();
        board_pageBtn1 = new javax.swing.JButton();
        tochatingPanalBtn1 = new javax.swing.JButton();
        tomoiminfoBtn1 = new javax.swing.JButton();
        toboardpanalBtn1 = new javax.swing.JButton();
        p7_chatingPanal = new javax.swing.JPanel();
        tochatingPanalBtn2 = new javax.swing.JButton();
        tomoiminfoBtn2 = new javax.swing.JButton();
        toboardpanalBtn2 = new javax.swing.JButton();
        talkBar = new javax.swing.JTextField();
        chating_pageBtn2 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        chatScreen = new javax.swing.JTextArea();
        p7_dogBtn = new javax.swing.JButton();
        p7_catBtn = new javax.swing.JButton();
        p7_wolfBtn = new javax.swing.JButton();
        p8_createdocument_Panal = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        addBoardBtn = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        glename = new javax.swing.JTextField();
        gle = new javax.swing.JTextField();
        p9_detailGlePanal = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        board_content = new javax.swing.JTextArea();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        content_name = new javax.swing.JLabel();
        board_writer_name = new javax.swing.JLabel();
        to_moimBtn = new javax.swing.JButton();
        comment_v = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        comment_area = new javax.swing.JTextArea();
        p10_mypagePanel = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        myname_v = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        myloc_v = new javax.swing.JLabel();
        update_loc_combobox = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        myhobby_v = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        update_hobby_combobox = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        mymoimList_table = new javax.swing.JTable();
        update_mypage_Btn = new javax.swing.JButton();
        tomainPanalBtn1 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        moimnum_v = new javax.swing.JTextField();
        moim_delete_Btn = new javax.swing.JButton();
        p10_time_v = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        dropMemBtn = new javax.swing.JButton();
        p11_moimjang = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        p11_infoBtn = new javax.swing.JButton();
        p11_joinBtn = new javax.swing.JButton();
        p11_deleteBtn = new javax.swing.JButton();
        moinBtn = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        p11_info = new javax.swing.JTextArea();
        jScrollPane10 = new javax.swing.JScrollPane();
        p11_joinTable = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        p11_delTable = new javax.swing.JTable();
        p11_join_v = new javax.swing.JTextField();
        p11_del_v = new javax.swing.JTextField();
        p11_deny_v = new javax.swing.JTextField();
        p11_denyBtn = new javax.swing.JButton();

        jLabel7.setText("jLabel7");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pp.setLayout(new java.awt.CardLayout());

        p1_login.setBackground(new java.awt.Color(247, 228, 228));

        jLabel11.setFont(new java.awt.Font("굴림", 0, 24)); // NOI18N
        jLabel11.setText("LOGIN");

        jLabel1.setText("ID : ");

        jLabel2.setText("PWD : ");

        loginBtn.setText("로그인");
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginBtnActionPerformed(evt);
            }
        });

        joinBtn.setText("회원가입");
        joinBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p1_loginLayout = new javax.swing.GroupLayout(p1_login);
        p1_login.setLayout(p1_loginLayout);
        p1_loginLayout.setHorizontalGroup(
            p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1_loginLayout.createSequentialGroup()
                .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p1_loginLayout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(jLabel11))
                    .addGroup(p1_loginLayout.createSequentialGroup()
                        .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p1_loginLayout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(p1_loginLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(log_pwd, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(p1_loginLayout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(30, 30, 30)
                                        .addComponent(log_id, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(p1_loginLayout.createSequentialGroup()
                                .addGap(144, 144, 144)
                                .addComponent(loginBtn)
                                .addGap(18, 18, 18)
                                .addComponent(joinBtn)))
                        .addGap(123, 123, 123)
                        .addComponent(show_hobby, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(321, Short.MAX_VALUE))
        );
        p1_loginLayout.setVerticalGroup(
            p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1_loginLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel11)
                .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p1_loginLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(log_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(log_pwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(p1_loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loginBtn)
                            .addComponent(joinBtn)))
                    .addGroup(p1_loginLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(show_hobby, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(369, Short.MAX_VALUE))
        );

        pp.add(p1_login, "c1");

        p2_adduser.setBackground(new java.awt.Color(255, 243, 253));

        jLabel3.setText("ID : ");

        jLabel4.setText("password : ");

        jLabel8.setText("지역 : ");

        jLabel10.setFont(new java.awt.Font("굴림", 1, 24)); // NOI18N
        jLabel10.setText("회원가입 패널");

        jLabel20.setText("취미 :");

        prevBtn.setText("< 이전 화면");
        prevBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevBtnActionPerformed(evt);
            }
        });

        nextBtn.setText("회원가입완료");
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });

        join_locCom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "서울", "경기", "인천", "그외" }));

        join_hobbyCom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "아웃도어/여행", "운동/스포츠", "사진/영상", "게임/오락", "요리/제조" }));

        jLabel22.setText(" 이름 : ");

        jLabel23.setText("주민번호 : ");

        jLabel24.setText("-");

        useMid.setText("중복확인");
        useMid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useMidActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p2_adduserLayout = new javax.swing.GroupLayout(p2_adduser);
        p2_adduser.setLayout(p2_adduserLayout);
        p2_adduserLayout.setHorizontalGroup(
            p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2_adduserLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(prevBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nextBtn)
                .addGap(40, 40, 40))
            .addGroup(p2_adduserLayout.createSequentialGroup()
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(p2_adduserLayout.createSequentialGroup()
                        .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(p2_adduserLayout.createSequentialGroup()
                                .addComponent(jumin_front, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addGap(7, 7, 7)
                                .addComponent(jumin_behind))
                            .addComponent(join_name1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(p2_adduserLayout.createSequentialGroup()
                            .addGap(99, 99, 99)
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(join_id, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(p2_adduserLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p2_adduserLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel4)
                            .addGap(18, 18, 18)
                            .addComponent(join_pwd, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(useMid)
                .addGap(104, 104, 104)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel20))
                .addGap(24, 24, 24)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(join_hobbyCom, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(join_locCom, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(391, Short.MAX_VALUE))
        );
        p2_adduserLayout.setVerticalGroup(
            p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2_adduserLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(join_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(join_locCom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(useMid))
                .addGap(18, 18, 18)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(join_pwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(join_hobbyCom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(join_name1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jumin_front, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jumin_behind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 295, Short.MAX_VALUE)
                .addGroup(p2_adduserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextBtn)
                    .addComponent(prevBtn))
                .addGap(101, 101, 101))
        );

        pp.add(p2_adduser, "c2");

        p3_createmoim.setBackground(new java.awt.Color(255, 254, 237));

        jLabel14.setBackground(new java.awt.Color(251, 248, 212));
        jLabel14.setFont(new java.awt.Font("굴림", 0, 24)); // NOI18N
        jLabel14.setText("모임 개설");

        jLabel16.setText("모임 이름 : ");

        jLabel15.setText("소개글 :");

        cmoimBtn.setText("모임 만들기");
        cmoimBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoimBtnActionPerformed(evt);
            }
        });

        main_page_Btn2.setText("이전 화면으로");
        main_page_Btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                main_page_Btn2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p3_createmoimLayout = new javax.swing.GroupLayout(p3_createmoim);
        p3_createmoim.setLayout(p3_createmoimLayout);
        p3_createmoimLayout.setHorizontalGroup(
            p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p3_createmoimLayout.createSequentialGroup()
                .addGroup(p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(p3_createmoimLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(main_page_Btn2))
                    .addGroup(p3_createmoimLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(p3_createmoimLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(cmoim_info, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(p3_createmoimLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmoim_name))
                            .addGroup(p3_createmoimLayout.createSequentialGroup()
                                .addGap(140, 140, 140)
                                .addComponent(jLabel14)))
                        .addGap(49, 552, Short.MAX_VALUE)))
                .addGap(42, 42, 42)
                .addComponent(cmoimBtn)
                .addGap(60, 60, 60))
        );
        p3_createmoimLayout.setVerticalGroup(
            p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p3_createmoimLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel14)
                .addGap(59, 59, 59)
                .addGroup(p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(cmoim_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cmoim_info, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(137, 137, 137)
                .addGroup(p3_createmoimLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoimBtn)
                    .addComponent(main_page_Btn2))
                .addContainerGap(378, Short.MAX_VALUE))
        );

        pp.add(p3_createmoim, "c3");

        p4_mainpanal.setBackground(new java.awt.Color(225, 229, 251));

        jLabel18.setText("검색단어 :");

        searchBtn.setText("검색");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        moim_crea_Btn.setText("+모임개설");
        moim_crea_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moim_crea_BtnActionPerformed(evt);
            }
        });

        mainPrintTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(mainPrintTable);

        tomymoimPanalBtn.setText("내 모임");
        tomymoimPanalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomymoimPanalBtnActionPerformed(evt);
            }
        });

        hobby1Btn.setText("아웃도어/여행");
        hobby1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hobby1BtnActionPerformed(evt);
            }
        });

        hobby2Btn.setText("운동/스포츠");
        hobby2Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hobby2BtnActionPerformed(evt);
            }
        });

        hobby3Btn.setText("사진/영상");
        hobby3Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hobby3BtnActionPerformed(evt);
            }
        });

        hobby4Byn.setText("게임/오락");
        hobby4Byn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hobby4BynActionPerformed(evt);
            }
        });

        hobby5Btn.setText("요리/제조");
        hobby5Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hobby5BtnActionPerformed(evt);
            }
        });

        logout_btn.setText("로그아웃");
        logout_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_btnActionPerformed(evt);
            }
        });

        jLabel6.setText("모임 번호 : ");

        moim_detail_Btn.setText("모임 상세보기");
        moim_detail_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moim_detail_BtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p4_mainpanalLayout = new javax.swing.GroupLayout(p4_mainpanal);
        p4_mainpanal.setLayout(p4_mainpanalLayout);
        p4_mainpanalLayout.setHorizontalGroup(
            p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p4_mainpanalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(p4_mainpanalLayout.createSequentialGroup()
                        .addComponent(main_img, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchnum, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moim_detail_Btn)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p4_mainpanalLayout.createSequentialGroup()
                        .addGap(0, 82, Short.MAX_VALUE)
                        .addGroup(p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p4_mainpanalLayout.createSequentialGroup()
                                .addComponent(hobby1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(hobby2Btn)
                                .addGap(18, 18, 18)
                                .addComponent(hobby3Btn)
                                .addGap(29, 29, 29)
                                .addComponent(hobby4Byn)
                                .addGap(18, 18, 18)
                                .addComponent(hobby5Btn)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(searchword, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(moim_crea_Btn))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p4_mainpanalLayout.createSequentialGroup()
                                .addComponent(logout_btn)
                                .addGap(41, 41, 41))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p4_mainpanalLayout.createSequentialGroup()
                                .addComponent(tomymoimPanalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(242, 242, 242))))))
        );
        p4_mainpanalLayout.setVerticalGroup(
            p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p4_mainpanalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(searchBtn)
                    .addComponent(moim_crea_Btn)
                    .addComponent(hobby1Btn)
                    .addComponent(hobby2Btn)
                    .addComponent(hobby3Btn)
                    .addComponent(hobby4Byn)
                    .addComponent(hobby5Btn))
                .addGap(52, 52, 52)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p4_mainpanalLayout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addGroup(p4_mainpanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(searchnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(moim_detail_Btn))
                        .addGap(39, 39, 39)
                        .addComponent(tomymoimPanalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p4_mainpanalLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(main_img, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(67, 67, 67)))
                .addComponent(logout_btn)
                .addGap(41, 41, 41))
        );

        pp.add(p4_mainpanal, "c4");

        p5_moimpage.setBackground(new java.awt.Color(229, 252, 249));

        jLabel12.setText("모임명 : ");

        jLabel13.setText("멤버리스트");

        moim_join_Btn.setText("모임 가입하기");
        moim_join_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moim_join_BtnActionPerformed(evt);
            }
        });

        moim_info.setColumns(20);
        moim_info.setRows(5);
        moim_info.setText("여기는 모임 설명이 쓰여있는 자리 ");
        jScrollPane1.setViewportView(moim_info);

        mainpageBtn.setText("메인 페이지로 돌아가기");
        mainpageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainpageBtnActionPerformed(evt);
            }
        });

        moimTitle.setText("모임명 출력자리");

        moimlist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(moimlist);

        tochatingPanalBtn.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        tochatingPanalBtn.setText("회원다중채팅");
        tochatingPanalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tochatingPanalBtnActionPerformed(evt);
            }
        });

        tomoiminfoBtn.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        tomoiminfoBtn.setText("모임장");
        tomoiminfoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomoiminfoBtnActionPerformed(evt);
            }
        });

        toboardpanalBtn.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        toboardpanalBtn.setText("게시판");
        toboardpanalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toboardpanalBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p5_moimpageLayout = new javax.swing.GroupLayout(p5_moimpage);
        p5_moimpage.setLayout(p5_moimpageLayout);
        p5_moimpageLayout.setHorizontalGroup(
            p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p5_moimpageLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p5_moimpageLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p5_moimpageLayout.createSequentialGroup()
                        .addGroup(p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1)
                            .addGroup(p5_moimpageLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(p5_moimpageLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(moim_join_Btn))
                                    .addGroup(p5_moimpageLayout.createSequentialGroup()
                                        .addComponent(moimTitle)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(45, 45, 45))
                    .addGroup(p5_moimpageLayout.createSequentialGroup()
                        .addComponent(jScrollPane4)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p5_moimpageLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mainpageBtn)
                .addGap(35, 35, 35))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p5_moimpageLayout.createSequentialGroup()
                .addComponent(tomoiminfoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(toboardpanalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tochatingPanalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        p5_moimpageLayout.setVerticalGroup(
            p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p5_moimpageLayout.createSequentialGroup()
                .addGroup(p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tomoiminfoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toboardpanalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tochatingPanalBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 111, Short.MAX_VALUE)
                .addGroup(p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(moim_join_Btn)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p5_moimpageLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(p5_moimpageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(moimTitle))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel13)
                .addGap(35, 35, 35)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(mainpageBtn)
                .addGap(91, 91, 91))
        );

        pp.add(p5_moimpage, "c5");

        board_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(board_Table);

        jLabel5.setText("상세 게시글 선택 창 :");

        detail_board_show_Btn.setText("상세게시글보기");
        detail_board_show_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detail_board_show_BtnActionPerformed(evt);
            }
        });

        create_board_Btn.setText("+게시글작성");
        create_board_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create_board_BtnActionPerformed(evt);
            }
        });

        board_pageBtn1.setText("메인 페이지로 돌아가기");
        board_pageBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                board_pageBtn1ActionPerformed(evt);
            }
        });

        tochatingPanalBtn1.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        tochatingPanalBtn1.setText("회원다중채팅");
        tochatingPanalBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tochatingPanalBtn1ActionPerformed(evt);
            }
        });

        tomoiminfoBtn1.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        tomoiminfoBtn1.setText("모임장");
        tomoiminfoBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomoiminfoBtn1ActionPerformed(evt);
            }
        });

        toboardpanalBtn1.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        toboardpanalBtn1.setText("게시판");
        toboardpanalBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toboardpanalBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p6_boardPanalLayout = new javax.swing.GroupLayout(p6_boardPanal);
        p6_boardPanal.setLayout(p6_boardPanalLayout);
        p6_boardPanalLayout.setHorizontalGroup(
            p6_boardPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p6_boardPanalLayout.createSequentialGroup()
                .addGroup(p6_boardPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, p6_boardPanalLayout.createSequentialGroup()
                        .addComponent(tomoiminfoBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toboardpanalBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tochatingPanalBtn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, p6_boardPanalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(p6_boardPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(p6_boardPanalLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(search_detail_board, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(detail_board_show_Btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(create_board_Btn)
                                .addGap(3, 3, 3))
                            .addComponent(board_pageBtn1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 869, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(183, Short.MAX_VALUE))
        );
        p6_boardPanalLayout.setVerticalGroup(
            p6_boardPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p6_boardPanalLayout.createSequentialGroup()
                .addGroup(p6_boardPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tomoiminfoBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toboardpanalBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tochatingPanalBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(p6_boardPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(search_detail_board, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detail_board_show_Btn)
                    .addComponent(create_board_Btn))
                .addGap(18, 18, 18)
                .addComponent(board_pageBtn1)
                .addContainerGap(227, Short.MAX_VALUE))
        );

        pp.add(p6_boardPanal, "c6");

        tochatingPanalBtn2.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        tochatingPanalBtn2.setText("회원다중채팅");
        tochatingPanalBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tochatingPanalBtn2ActionPerformed(evt);
            }
        });

        tomoiminfoBtn2.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        tomoiminfoBtn2.setText("모임장");
        tomoiminfoBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomoiminfoBtn2ActionPerformed(evt);
            }
        });

        toboardpanalBtn2.setFont(new java.awt.Font("굴림", 0, 48)); // NOI18N
        toboardpanalBtn2.setText("게시판");
        toboardpanalBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toboardpanalBtn2ActionPerformed(evt);
            }
        });

        talkBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                talkBarActionPerformed(evt);
            }
        });

        chating_pageBtn2.setText("메인 페이지로 돌아가기");
        chating_pageBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chating_pageBtn2ActionPerformed(evt);
            }
        });

        chatScreen.setColumns(20);
        chatScreen.setRows(5);
        jScrollPane5.setViewportView(chatScreen);

        p7_dogBtn.setText("왈왈");
        p7_dogBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p7_dogBtnActionPerformed(evt);
            }
        });

        p7_catBtn.setText("야옹야옹");
        p7_catBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p7_catBtnActionPerformed(evt);
            }
        });

        p7_wolfBtn.setText("아우~~");
        p7_wolfBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p7_wolfBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p7_chatingPanalLayout = new javax.swing.GroupLayout(p7_chatingPanal);
        p7_chatingPanal.setLayout(p7_chatingPanalLayout);
        p7_chatingPanalLayout.setHorizontalGroup(
            p7_chatingPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p7_chatingPanalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(p7_chatingPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p7_chatingPanalLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(chating_pageBtn2)
                        .addGap(25, 25, 25))
                    .addGroup(p7_chatingPanalLayout.createSequentialGroup()
                        .addGroup(p7_chatingPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p7_chatingPanalLayout.createSequentialGroup()
                                .addComponent(talkBar, javax.swing.GroupLayout.PREFERRED_SIZE, 723, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane5)
                            .addGroup(p7_chatingPanalLayout.createSequentialGroup()
                                .addComponent(tomoiminfoBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(toboardpanalBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tochatingPanalBtn2)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(p7_chatingPanalLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(p7_dogBtn)
                .addGap(42, 42, 42)
                .addComponent(p7_catBtn)
                .addGap(48, 48, 48)
                .addComponent(p7_wolfBtn)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        p7_chatingPanalLayout.setVerticalGroup(
            p7_chatingPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p7_chatingPanalLayout.createSequentialGroup()
                .addGroup(p7_chatingPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tomoiminfoBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toboardpanalBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tochatingPanalBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(p7_chatingPanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p7_dogBtn)
                    .addComponent(p7_catBtn)
                    .addComponent(p7_wolfBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(talkBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chating_pageBtn2)
                .addGap(22, 22, 22))
        );

        pp.add(p7_chatingPanal, "c7");

        jButton1.setText("<게시판으로");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        addBoardBtn.setText("등록");
        addBoardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBoardBtnActionPerformed(evt);
            }
        });

        jLabel9.setText("제목 :");

        jLabel19.setText("내용 :");

        javax.swing.GroupLayout p8_createdocument_PanalLayout = new javax.swing.GroupLayout(p8_createdocument_Panal);
        p8_createdocument_Panal.setLayout(p8_createdocument_PanalLayout);
        p8_createdocument_PanalLayout.setHorizontalGroup(
            p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p8_createdocument_PanalLayout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addGroup(p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, p8_createdocument_PanalLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(330, 330, 330)
                        .addComponent(addBoardBtn))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, p8_createdocument_PanalLayout.createSequentialGroup()
                        .addGroup(p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel19))
                        .addGap(27, 27, 27)
                        .addGroup(p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(gle, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                            .addComponent(glename))))
                .addContainerGap(332, Short.MAX_VALUE))
        );
        p8_createdocument_PanalLayout.setVerticalGroup(
            p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p8_createdocument_PanalLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(addBoardBtn))
                .addGap(58, 58, 58)
                .addGroup(p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(glename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55)
                .addGroup(p8_createdocument_PanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(gle, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(146, Short.MAX_VALUE))
        );

        pp.add(p8_createdocument_Panal, "c8");

        board_content.setColumns(20);
        board_content.setRows(5);
        jScrollPane6.setViewportView(board_content);

        jLabel25.setText("작성자 : ");

        jLabel26.setText("게시글 제목 : ");

        content_name.setText("게시글 제목이 들어가는 곳");

        board_writer_name.setText("작성자 이름");

        to_moimBtn.setText("< 뒤로가기");
        to_moimBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                to_moimBtnActionPerformed(evt);
            }
        });

        comment_v.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comment_vActionPerformed(evt);
            }
        });

        jLabel27.setText("댓글창");

        comment_area.setColumns(20);
        comment_area.setRows(5);
        jScrollPane9.setViewportView(comment_area);

        javax.swing.GroupLayout p9_detailGlePanalLayout = new javax.swing.GroupLayout(p9_detailGlePanal);
        p9_detailGlePanal.setLayout(p9_detailGlePanalLayout);
        p9_detailGlePanalLayout.setHorizontalGroup(
            p9_detailGlePanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p9_detailGlePanalLayout.createSequentialGroup()
                .addGroup(p9_detailGlePanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p9_detailGlePanalLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(board_writer_name)
                        .addGap(104, 104, 104)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(content_name))
                    .addGroup(p9_detailGlePanalLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(p9_detailGlePanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel27)
                            .addComponent(to_moimBtn)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                            .addComponent(jScrollPane9)
                            .addComponent(comment_v))))
                .addContainerGap(380, Short.MAX_VALUE))
        );
        p9_detailGlePanalLayout.setVerticalGroup(
            p9_detailGlePanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p9_detailGlePanalLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(to_moimBtn)
                .addGap(89, 89, 89)
                .addGroup(p9_detailGlePanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(content_name)
                    .addComponent(board_writer_name))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel27)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(comment_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        pp.add(p9_detailGlePanal, "c9");

        jLabel28.setFont(new java.awt.Font("굴림", 0, 36)); // NOI18N
        jLabel28.setText("나의 정보");

        jLabel29.setText("내 이름 : ");

        myname_v.setText("[ 이 름 ]");

        jLabel31.setText("내가 선택한 지역 : ");

        myloc_v.setText("[ 지 역 ]");

        update_loc_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "서울", "경기", "인천", "그 외" }));

        jLabel33.setText("변경 할 지역 : ");

        jLabel34.setText("내가 선택한 취미 : ");

        myhobby_v.setText("[ 취 미 ]");

        jLabel36.setText("변경 할 취미 : ");

        update_hobby_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "아웃도어/여행", "운동/스포츠", "사진/영상", "게임/오락", "요리/제조" }));

        jLabel37.setText("내가 가입한 모임");

        mymoimList_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane8.setViewportView(mymoimList_table);

        update_mypage_Btn.setText("변경 내용 저장");
        update_mypage_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_mypage_BtnActionPerformed(evt);
            }
        });

        tomainPanalBtn1.setFont(new java.awt.Font("굴림", 0, 36)); // NOI18N
        tomainPanalBtn1.setText("소모임");
        tomainPanalBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomainPanalBtn1ActionPerformed(evt);
            }
        });

        jLabel30.setText("모임 번호");

        moim_delete_Btn.setText("모임 탈퇴");
        moim_delete_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moim_delete_BtnActionPerformed(evt);
            }
        });

        p10_time_v.setText("[ 가 입 기간");

        jLabel38.setText("가입 기간 :");

        dropMemBtn.setText("회원 탈퇴");
        dropMemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropMemBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p10_mypagePanelLayout = new javax.swing.GroupLayout(p10_mypagePanel);
        p10_mypagePanel.setLayout(p10_mypagePanelLayout);
        p10_mypagePanelLayout.setHorizontalGroup(
            p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p10_mypagePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moimnum_v, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(moim_delete_Btn)
                .addGap(299, 299, 299))
            .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(update_mypage_Btn))
                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(myname_v))
                            .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addGap(18, 18, 18)
                                        .addComponent(myloc_v))
                                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel34)
                                        .addGap(18, 18, 18)
                                        .addComponent(myhobby_v)))
                                .addGap(143, 143, 143)
                                .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel36)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(update_hobby_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(update_loc_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addGap(18, 18, 18)
                                .addComponent(p10_time_v, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(232, 232, 232))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p10_mypagePanelLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                                .addGap(92, 92, 92)
                                .addComponent(tomainPanalBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(dropMemBtn)
                .addGap(61, 61, 61))
        );
        p10_mypagePanelLayout.setVerticalGroup(
            p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p10_mypagePanelLayout.createSequentialGroup()
                .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dropMemBtn))
                    .addGroup(p10_mypagePanelLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(update_mypage_Btn))
                        .addGap(38, 38, 38)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(myname_v))
                        .addGap(31, 31, 31)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(myloc_v)
                            .addComponent(update_loc_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addGap(35, 35, 35)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(myhobby_v)
                            .addComponent(jLabel36)
                            .addComponent(update_hobby_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(p10_time_v, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(jLabel37)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addGroup(p10_mypagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30)
                            .addComponent(moimnum_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(moim_delete_Btn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tomainPanalBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25))
        );

        pp.add(p10_mypagePanel, "c10");

        jLabel32.setFont(new java.awt.Font("굴림", 1, 36)); // NOI18N
        jLabel32.setText("모임장 페이지");

        p11_infoBtn.setText("소개글 변경");
        p11_infoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p11_infoBtnActionPerformed(evt);
            }
        });

        p11_joinBtn.setText("가입승인");
        p11_joinBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p11_joinBtnActionPerformed(evt);
            }
        });

        p11_deleteBtn.setText("회원강퇴");
        p11_deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p11_deleteBtnActionPerformed(evt);
            }
        });

        moinBtn.setText("모임페이지로");
        moinBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moinBtnActionPerformed(evt);
            }
        });

        p11_info.setColumns(20);
        p11_info.setRows(5);
        jScrollPane7.setViewportView(p11_info);

        p11_joinTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane10.setViewportView(p11_joinTable);

        p11_delTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane11.setViewportView(p11_delTable);

        p11_denyBtn.setText("승인거부");
        p11_denyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p11_denyBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p11_moimjangLayout = new javax.swing.GroupLayout(p11_moimjang);
        p11_moimjang.setLayout(p11_moimjangLayout);
        p11_moimjangLayout.setHorizontalGroup(
            p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p11_moimjangLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(p11_moimjangLayout.createSequentialGroup()
                .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p11_moimjangLayout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7)
                            .addComponent(jScrollPane10)
                            .addComponent(jScrollPane11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p11_moimjangLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(p11_del_v, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p11_join_v, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(p11_deny_v, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p11_moimjangLayout.createSequentialGroup()
                        .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(p11_infoBtn)
                            .addComponent(p11_joinBtn, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(285, 285, 285))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, p11_moimjangLayout.createSequentialGroup()
                        .addComponent(p11_deleteBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(moinBtn)
                        .addGap(109, 109, 109))
                    .addGroup(p11_moimjangLayout.createSequentialGroup()
                        .addComponent(p11_denyBtn)
                        .addContainerGap())))
        );
        p11_moimjangLayout.setVerticalGroup(
            p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p11_moimjangLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(p11_moimjangLayout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(p11_infoBtn))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p11_join_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p11_joinBtn))
                .addGap(18, 18, 18)
                .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p11_deny_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p11_denyBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(p11_moimjangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moinBtn)
                    .addComponent(p11_deleteBtn)
                    .addComponent(p11_del_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );

        pp.add(p11_moimjang, "c11");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // p1 - 회원가입 페이지 이동버튼
    private void joinBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinBtnActionPerformed
        card.show(pp, "c2");
    }//GEN-LAST:event_joinBtnActionPerformed
            
    private void loginBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginBtnActionPerformed
      //p1 - id, pwd 입력 후 로그인하는 버튼
        mem = new AMember();
       String id = log_id.getText();
       String pwd = log_pwd.getText();
       //checkIdMd ( id,pwd 체크 true = return 1 , false = return 0 , 체크할 값이 없다면 -1 반환)
        int a = md.checkIdMd(id, pwd);        
        if(a==1){
            //checkIdMd -> return 1일경우
        mem = md.loginMd(mem,id); //loginMd (memberdto에 id에 해당하는 유저의 정보를 받아 저장합니다) 
        JOptionPane.showMessageDialog(this, "환영합니다","로그인 성공!" , JOptionPane.INFORMATION_MESSAGE);
        //로그인이 성공하게되면
        //groupListMd -> 유저의 정보 중 취미에 해당하는 번호를 우선순위로 메인페이지에 출력하는 메서드입니다
        groupList(md.groupListMd(mem));
        mainImgShow(mem.getMhobby());
        card.show(pp, "c4");
        }else if(a ==0){
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호가 불일치합니다.","로그인 실패!",JOptionPane.ERROR_MESSAGE);
        }else{
             JOptionPane.showMessageDialog(this, "아이디가 없습니다.","???",JOptionPane.ERROR_MESSAGE);
        }
  
    }//GEN-LAST:event_loginBtnActionPerformed
   
    private void prevBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevBtnActionPerformed
        //p2 - 회원가입 페이지에서 로그인 화면으로 이동하는 화면 버튼
        card.show(pp, "c1");
        log_id.setText("");
        log_pwd.setText("");
    }//GEN-LAST:event_prevBtnActionPerformed

    
    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
       //p2 - 회원가입완료 버튼
        try {        
        if(join_id.getText().trim().split(" ").length !=1 || join_pwd.getText().trim().split(" ").length !=1
           || join_name1.getText().split(" ").length !=1 || jumin_front.getText().split(" ").length !=1
           || jumin_behind.getText().split(" ").length !=1
                ){ // 입력된 사용자의 정보란에 공백칸이 있을경우
            throw new NotSpace(); // 오류를 발생시키고 NotSpace로 throw 하는 메서드입니다 
        }
        Integer.parseInt(jumin_front.getText());
        Integer.parseInt(jumin_behind.getText());
        if(jumin_front.getText().length()!=6 || jumin_behind.getText().length()!=7){ // 주민번호의 앞자리 6 뒷자리 7이 아닐경우
        throw new Jumin();// 오류를 발생시키고 Jumin로 throw 하는 메서드입니다 
        }
        AMember mem = new AMember();
        mem.setMid(join_id.getText().trim());
        mem.setMpwd(join_pwd.getText().trim());
        mem.setMname(join_name1.getText().trim());
        mem.setMloc((String) join_locCom.getSelectedItem());
        mem.setMjumin(jumin_front.getText().trim()+ jLabel24.getText()+jumin_behind.getText().trim());
        mem.setMhobby(join_hobbyCom.getSelectedIndex()+1);
        int a = md.joinMemberMd(mem); //사용자의 정보를 DB에 입력시키는 메서드입니다
        if(a == 0){
        JOptionPane.showMessageDialog(rootPane, "회원가입이 완료되었습니다!","환영합니다",JOptionPane.INFORMATION_MESSAGE);
        card.show(pp,"c1"); //가입이 완료되면 로그인페이지로 돌아감
        }else{
            JOptionPane.showMessageDialog(null, "아이디가 존재합니다", "경고!!", JOptionPane.ERROR_MESSAGE);
        }
        } catch (NumberFormatException e) {
           JOptionPane.showMessageDialog(rootPane, "주민번호를 제대로 입력하세요","경고!!",JOptionPane.ERROR_MESSAGE);
        } catch(Jumin e){
           JOptionPane.showMessageDialog(rootPane, "주민번호자릿수가 맞지 않습니다","경고!!",JOptionPane.ERROR_MESSAGE);
        } catch(NotSpace e){
           JOptionPane.showMessageDialog(rootPane, "공백을 입력할수 없습니다","경고!!",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_nextBtnActionPerformed
   
    private void mainpageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainpageBtnActionPerformed
        //모임상세페이지에서 메인페이지로 이동하는 버튼
        groupList(md.groupListMd(mem));
        card.show(pp, "c4");
        
    }//GEN-LAST:event_mainpageBtnActionPerformed

    private void cmoimBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoimBtnActionPerformed
       //모임 개설하기 버튼
        AGroup a = new AGroup();
        
        a.setGname(cmoim_name.getText());
        a.setGinfo(cmoim_info.getText());

        md.addGroupMd(mem, a);
        //addGroupMd -> 모임 개설하기 메서드(ref = 모임을 만드는 회원의 정보를 가지고 )
        
        groupList(md.groupListMd(mem));

        card.show(pp,"c4");
    }//GEN-LAST:event_cmoimBtnActionPerformed
   
    private void moim_join_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moim_join_BtnActionPerformed
         //회원이 모임을 가입신청 버튼
        AJoin aj = new AJoin();
        aj.setAgroup(group);
        aj.setAmember(mem);
        int ck = md.ckJoinDelay(aj);
        if(ck==1){
        md.joinMd(aj);
        JOptionPane.showMessageDialog(board_content, "가입신청 완료. 모임장이 수락할때까지 대기해주세요");
        } else if(ck==-1){
        JOptionPane.showMessageDialog(board_content, "이미 가입한 그룹입니다");
        }
        else{
        JOptionPane.showMessageDialog(board_content, "이미 가입 신청을 했습니다.");
        }
        groupdetailList(md.listGroupMd(group.getGroupnum())); //가입된 멤버 jtable에 출력
        System.out.println(group.getGroupnum());
    }//GEN-LAST:event_moim_join_BtnActionPerformed

    private void main_page_Btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_main_page_Btn2ActionPerformed
        card.show(pp, "c4");
    }//GEN-LAST:event_main_page_Btn2ActionPerformed
    // 모임장 버튼
    private void tomoiminfoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomoiminfoBtnActionPerformed
    p11_info.setText(group.getGinfo());
    AJoin aj = new AJoin();
     aj.setAgroup(group);
     aj.setAmember(mem);
     int jangck = md.ckMoimjang(aj);
    if(jangck ==1){    
    getApplicantList(md.getApplicantList(group));
    card.show(pp, "c11");
    }else{
    JOptionPane.showMessageDialog(board_content, "모임장만 볼수있습니다.");
    }   
    }//GEN-LAST:event_tomoiminfoBtnActionPerformed
// 모임페이지에서 메인메이지로
    private void board_pageBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_board_pageBtn1ActionPerformed
        groupList(md.groupListMd(mem));
        card.show(pp, "c4");
        
    }//GEN-LAST:event_board_pageBtn1ActionPerformed

    private void tomoiminfoBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomoiminfoBtn1ActionPerformed
    p11_info.setText(group.getGinfo());
    AJoin aj = new AJoin();
     aj.setAgroup(group);
     aj.setAmember(mem);
     int jangck = md.ckMoimjang(aj);
    if(jangck ==1){    
    getApplicantList(md.getApplicantList(group));
    card.show(pp, "c11");
    }else{
    JOptionPane.showMessageDialog(board_content, "모임장만 볼수있습니다.");
    }         
    }//GEN-LAST:event_tomoiminfoBtn1ActionPerformed

    private void tomoiminfoBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomoiminfoBtn2ActionPerformed
    p11_info.setText(group.getGinfo());
    AJoin aj = new AJoin();
     aj.setAgroup(group);
     aj.setAmember(mem);
     int jangck = md.ckMoimjang(aj);
    if(jangck ==1){    
    getApplicantList(md.getApplicantList(group));
    card.show(pp, "c11");
    }else{
    JOptionPane.showMessageDialog(board_content, "모임장만 볼수있습니다.");
    }   
    }//GEN-LAST:event_tomoiminfoBtn2ActionPerformed

    private void chating_pageBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chating_pageBtn2ActionPerformed
    groupList(md.groupListMd(mem));
    card.show(pp, "c5");
    }//GEN-LAST:event_chating_pageBtn2ActionPerformed
    // 내 정보 변경 버튼
    private void update_mypage_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_mypage_BtnActionPerformed
        String mloc = (String) update_loc_combobox.getSelectedItem();
        int mhobby = update_hobby_combobox.getSelectedIndex()+1;       
        
       int a = JOptionPane.showConfirmDialog(rootPane, "변경하시겠습니까??", "확인!!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
       if(a == 0){
           md.updateMyInfoMd(mem, mloc, mhobby);
        
        myname_v.setText(mem.getMname());
        myloc_v.setText(mem.getMloc());
        myhobby_v.setText(mem.getHobby().getHname());
        p10_time_v.setText(mem.getJoindate());
       }
           
       
       
    }//GEN-LAST:event_update_mypage_BtnActionPerformed

    private void tomainPanalBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomainPanalBtn1ActionPerformed
        groupList(md.groupListMd(mem));
        mainImgShow(mem.getMhobby());
        card.show(pp, "c4");
    }//GEN-LAST:event_tomainPanalBtn1ActionPerformed

    private void create_board_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create_board_BtnActionPerformed
       // 게시글 작성 페이지 이동
       card.show(pp, "c8");
    }//GEN-LAST:event_create_board_BtnActionPerformed

    private void addBoardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBoardBtnActionPerformed
        // 게시글 작성 버튼
        AJoin aj = new AJoin();
        aj.setAmember(mem);
        aj.setAgroup(group);
        Board b = new Board();
        b.setSubject(glename.getText());
        b.setContent(gle.getText());
        md.addBoardMd(b,aj);
        JOptionPane.showMessageDialog(rootPane, "게시글이 작성되었습니다.","!!?",JOptionPane.INFORMATION_MESSAGE);
        listContent();
        card.show(pp, "c6");
    }//GEN-LAST:event_addBoardBtnActionPerformed
    // 게시판 목록으로 이동하는 버튼
    private void toboardpanalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toboardpanalBtnActionPerformed
        AJoin aj = new AJoin();
        aj.setAgroup(group);
        aj.setAmember(mem);
        int ck = md.ckGJlList(aj);
        if(ck==1){
        listContent();
        card.show(pp, "c6");
        }else{
            JOptionPane.showMessageDialog(board_content, "가입하지 않은 모임입니다. 가입을 먼저해주세요.");
        }
    }//GEN-LAST:event_toboardpanalBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // 게시판 메인 페이지로 가는 버튼
        card.show(pp, "c6");
    }//GEN-LAST:event_jButton1ActionPerformed
    // 상세 게시글 보기
    private void detail_board_show_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detail_board_show_BtnActionPerformed
       try {
        BoardComm bc = new BoardComm();
        board = new Board();
        board.setWlist(Integer.parseInt(search_detail_board.getText()));
        board = md.getDetail(board,group);
        bc.setBo(board);
        board_writer_name.setText(board.getAmember().getMname());
        content_name.setText(board.getSubject());
        board_content.setText(board.getContent());
        printCommentlist(bc);
        card.show(pp, "c9");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(board_content, "숫자를 입력해주세요");
        } catch(NullPointerException e){
            JOptionPane.showMessageDialog(board_content, "작성된 게시글 번호가 없습니다.");
        }
    }//GEN-LAST:event_detail_board_show_BtnActionPerformed
 // 게시글 상세보기에서 게시글 목록으로 뒤로가는 버튼
    private void to_moimBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_to_moimBtnActionPerformed
        card.show(pp, "c6");
    }//GEN-LAST:event_to_moimBtnActionPerformed

    private void comment_vActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comment_vActionPerformed
       //댓글 등록,출력 
        AJoin aj = new AJoin();
        aj.setAmember(mem);
        aj.setAgroup(group);
        BoardComm bc = new BoardComm();
        bc.setBo(board);
        bc.setContent(comment_v.getText());
        md.addCommMd(bc, aj);
        printCommentlist(bc);
        comment_v.setText("");
    }//GEN-LAST:event_comment_vActionPerformed

    private void toboardpanalBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toboardpanalBtn1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toboardpanalBtn1ActionPerformed

    private void tochatingPanalBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tochatingPanalBtn1ActionPerformed
        chatConn();
    }//GEN-LAST:event_tochatingPanalBtn1ActionPerformed

    private void tochatingPanalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tochatingPanalBtnActionPerformed
        AJoin aj = new AJoin();
        aj.setAgroup(group);
        aj.setAmember(mem);
        int ck = md.ckGJlList(aj);
        if(ck==1){
        chatConn();
        card.show(pp, "c7");
        }else{
            JOptionPane.showMessageDialog(board_content, "가입하지 않은 모임입니다. 가입을 먼저해주세요.");
        }



    }//GEN-LAST:event_tochatingPanalBtnActionPerformed

    private void talkBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_talkBarActionPerformed
        String msg = talkBar.getText();
        StringBuilder sb = new StringBuilder();
        sb.append("talk").append("/").append(group.getGroupnum()).append("/").append(mem.getMid()).append("/").append(msg);
        pw.println(sb.toString());
        talkBar.setText("");
    }//GEN-LAST:event_talkBarActionPerformed

    private void moim_delete_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moim_delete_BtnActionPerformed
        // 내정보페이지에서 모임 탈퇴하기 버튼
       
         int delnum = Integer.parseInt(moimnum_v.getText());
        AJoin aj = new AJoin();
        aj.setMembernum(mem.getMembernum());
        aj.setGroupnum(delnum);
        int num = 0;
        int b = md.ckMoimjang2(aj);
        if(b==1){ // 모임장이 모임 삭제
            int i = JOptionPane.showConfirmDialog(rootPane, "당신은 모임장입니다 모임을 삭제하시겠습니까?", "확인!!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                if(i==0){
                md.deleteMyMoim2(delnum);
                JOptionPane.showMessageDialog(board_content, "모임이 삭제되었습니다.");
                 myGroupList(md.listMyGroupMd(mem.getMembernum()));
                }
        } else{
            num = md.deleteMyMoimMd(aj);
            if(num==1){
                JOptionPane.showMessageDialog(null, aj.getGroupnum()+"번 모임을 탈퇴하였습니다.", "알림창", JOptionPane.INFORMATION_MESSAGE);
            } else{
                 JOptionPane.showMessageDialog(null, "가입한 모임번호가 아닙니다.","알림창", JOptionPane.ERROR_MESSAGE);
            }
            
        }
        
    }//GEN-LAST:event_moim_delete_BtnActionPerformed

    private void tochatingPanalBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tochatingPanalBtn2ActionPerformed
       chatConn();
    }//GEN-LAST:event_tochatingPanalBtn2ActionPerformed

    private void p11_joinBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p11_joinBtnActionPerformed
          try{
        int joinnum = Integer.parseInt(p11_join_v.getText());
        AJoin aj = new AJoin();
        AMember m = new AMember();
        m.setMembernum(joinnum);
        aj.setAmember(m);
        aj.setAgroup(group);
        int a = md.getAplicantInfo(aj);
        if(a==1){
        md.acceptJoin(joinnum,group.getGroupnum());
        JOptionPane.showMessageDialog(board_content, "가입이 승인되었습니다.");
        getApplicantList(md.getApplicantList(group));
        groupdetailList(md.listGroupMd(group.getGroupnum()));
        }else{
           JOptionPane.showMessageDialog(board_content, "회원 번호를 제대로 입력해주세요.");
        }
        } catch(NumberFormatException e){
            System.out.println("숫자를 입력해주세요");
        }
    }//GEN-LAST:event_p11_joinBtnActionPerformed

    private void p11_denyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p11_denyBtnActionPerformed
    try{
        AJoin aj = new AJoin();
        AMember m = new AMember();
        int denynum = Integer.parseInt(p11_deny_v.getText());
        m.setMembernum(denynum);
        aj.setAmember(m);
        aj.setAgroup(group);
        int a = JOptionPane.showConfirmDialog(rootPane, "정말 가입 거부하시겠습니까??", "확인!!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(a==0){
        md.denyJoin(aj);
        JOptionPane.showMessageDialog(board_content, "가입이 거부되었습니다.");
        getApplicantList(md.getApplicantList(group));
        }
    }catch(NumberFormatException e){
            System.out.println("숫자를 입력해주세요");
        }
    }//GEN-LAST:event_p11_denyBtnActionPerformed

    private void moinBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moinBtnActionPerformed
       card.show(pp, "c5");
    }//GEN-LAST:event_moinBtnActionPerformed

    private void p7_dogBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p7_dogBtnActionPerformed
       StringBuilder sb = new StringBuilder();
       String msg = new Dog().cry();
       sb.append("talk").append("/").append(group.getGroupnum()).append("/").append(mem.getMid()).append("/").append(msg);
       pw.println(sb.toString());
    }//GEN-LAST:event_p7_dogBtnActionPerformed

    private void p7_catBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p7_catBtnActionPerformed
       StringBuilder sb = new StringBuilder();
       String msg = new Cat().cry();
       sb.append("talk").append("/").append(group.getGroupnum()).append("/").append(mem.getMid()).append("/").append(msg);
       pw.println(sb.toString());
    }//GEN-LAST:event_p7_catBtnActionPerformed

    private void p7_wolfBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p7_wolfBtnActionPerformed
       StringBuilder sb = new StringBuilder();
       String msg = new Wolf().cry();
       sb.append("talk").append("/").append(group.getGroupnum()).append("/").append(mem.getMid()).append("/").append(msg);
       pw.println(sb.toString());
    }//GEN-LAST:event_p7_wolfBtnActionPerformed

    private void moim_detail_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moim_detail_BtnActionPerformed
        //모임 번호로 search한 모임 상세페이지 이동버튼
        try{
        int moimnum = Integer.parseInt(searchnum.getText().trim()) ;
        System.out.println(moimnum);
        group = new AGroup();
        group.setGroupnum(moimnum);
        int ckgroup = md.ckGroup(group);
        group = md.enterGroupMd(group);
        moimTitle.setText(group.getGname());
        moim_info.setText(group.getGinfo());
        if(ckgroup == 1){
        groupdetailList(md.listGroupMd(group.getGroupnum()));
        card.show(pp, "c5");
        }else{
        JOptionPane.showMessageDialog(board_content, group.getGroupnum()+"는 없는 그룹 번호입니다.");
        }
        } catch(NumberFormatException e){
           JOptionPane.showMessageDialog(board_content, "그룹번호를 입력해주세요.");
        }  
    }//GEN-LAST:event_moim_detail_BtnActionPerformed

   //메인페이지에서 로그아웃 버튼
    private void logout_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_btnActionPerformed
        card.show(pp, "c1");
        
        
        
    }//GEN-LAST:event_logout_btnActionPerformed

    private void hobby5BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hobby5BtnActionPerformed
        // 취미버튼 1
        hobbynum = 5;
        mainImgShow(hobbynum);
        groupList(md.selectGroupHobby(hobbynum));
    }//GEN-LAST:event_hobby5BtnActionPerformed

    private void hobby4BynActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hobby4BynActionPerformed
        // 취미버튼 1
        hobbynum = 4;
        mainImgShow(hobbynum);
        groupList(md.selectGroupHobby(hobbynum));
    }//GEN-LAST:event_hobby4BynActionPerformed

    private void hobby3BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hobby3BtnActionPerformed
        // 취미버튼 1
        hobbynum = 3;
        mainImgShow(hobbynum);
        groupList(md.selectGroupHobby(hobbynum));
    }//GEN-LAST:event_hobby3BtnActionPerformed

    private void hobby2BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hobby2BtnActionPerformed
        // 취미버튼 1
        hobbynum = 2;
        mainImgShow(hobbynum);
        groupList(md.selectGroupHobby(hobbynum));
    }//GEN-LAST:event_hobby2BtnActionPerformed

    private void hobby1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hobby1BtnActionPerformed
        // 취미버튼 1
        hobbynum = 1;
        mainImgShow(hobbynum);
        groupList(md.selectGroupHobby(hobbynum));
    }//GEN-LAST:event_hobby1BtnActionPerformed

    private void moim_crea_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moim_crea_BtnActionPerformed
        //p4 - 모임 개설 페이지로 이동 버튼
        card.show(pp, "c3");
    }//GEN-LAST:event_moim_crea_BtnActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        // p4 - 모임 검색하기
        String res = searchword.getText();
        mainImgShow(mem.getMhobby());
        groupList(md.detailGroup(res));
    }//GEN-LAST:event_searchBtnActionPerformed

    private void tomymoimPanalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomymoimPanalBtnActionPerformed
        myname_v.setText(mem.getMname());
        myloc_v.setText(mem.getMloc());
        myhobby_v.setText(mem.getHobby().getHname());
        p10_time_v.setText(mem.getJoindate());
        myGroupList(md.listMyGroupMd(mem.getMembernum()));
        card.show(pp, "c10");
        
    }//GEN-LAST:event_tomymoimPanalBtnActionPerformed

    private void toboardpanalBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toboardpanalBtn2ActionPerformed
        card.show(pp, "c6");
    }//GEN-LAST:event_toboardpanalBtn2ActionPerformed

    private void p11_deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p11_deleteBtnActionPerformed
        AJoin aj = new AJoin();
        int kicknum = Integer.parseInt(p11_del_v.getText());
        AMember m = new AMember();
        m.setMembernum(kicknum);
        aj.setAmember(m);
        aj.setAgroup(group);
        int a = md.kickPpl(aj);
        if(a==1){
        int b = JOptionPane.showConfirmDialog(rootPane, "정말 추방하시겠습니까??", "확인!!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(b==0){
            JOptionPane.showMessageDialog(this, "회원 추방이 완료되었습니다.");
            groupdetailList(md.listGroupMd(group.getGroupnum()));
            }
        }else if(a==0){
            JOptionPane.showMessageDialog(board_content, "모임장 본인은 추방할수 없습니다.");
        }
        
    }//GEN-LAST:event_p11_deleteBtnActionPerformed

    private void p11_infoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p11_infoBtnActionPerformed
        group.setGinfo(p11_info.getText());
        md.changeGinfo(group);
        p11_info.setText(group.getGinfo());
        JOptionPane.showMessageDialog(board_content, "소개글이 변경되었습니다");
    }//GEN-LAST:event_p11_infoBtnActionPerformed

    private void useMidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useMidActionPerformed
        AMember a = new AMember();
            if(md.chkUsedId(join_id.getText()) == true){
                JOptionPane.showMessageDialog(board_content, "사용중인 아이디");
                join_id.setText("");
                return;
            }else{
                JOptionPane.showMessageDialog(board_content, "사용가능한 아이디");
            }
    }//GEN-LAST:event_useMidActionPerformed

    private void dropMemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropMemBtnActionPerformed
        int ck = JOptionPane.showConfirmDialog(board_content, "탈퇴하시겠습니까?", "탈퇴", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(ck == 0){
            md.dropMemberMd(mem);
            card.show(pp, "c1");
        }else{
            
        }
    }//GEN-LAST:event_dropMemBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
             ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Asemi_ui_1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBoardBtn;
    private javax.swing.JTable board_Table;
    private javax.swing.JTextArea board_content;
    private javax.swing.JButton board_pageBtn1;
    private javax.swing.JLabel board_writer_name;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JTextArea chatScreen;
    private javax.swing.JButton chating_pageBtn2;
    private javax.swing.JButton cmoimBtn;
    private javax.swing.JTextField cmoim_info;
    private javax.swing.JTextField cmoim_name;
    private javax.swing.JTextArea comment_area;
    private javax.swing.JTextField comment_v;
    private javax.swing.JLabel content_name;
    private javax.swing.JButton create_board_Btn;
    private javax.swing.JButton detail_board_show_Btn;
    private javax.swing.JButton dropMemBtn;
    private javax.swing.JTextField gle;
    private javax.swing.JTextField glename;
    private javax.swing.JButton hobby1Btn;
    private javax.swing.JButton hobby2Btn;
    private javax.swing.JButton hobby3Btn;
    private javax.swing.JButton hobby4Byn;
    private javax.swing.JButton hobby5Btn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton joinBtn;
    private javax.swing.JComboBox<String> join_hobbyCom;
    private javax.swing.JTextField join_id;
    private javax.swing.JComboBox<String> join_locCom;
    private javax.swing.JTextField join_name1;
    private javax.swing.JTextField join_pwd;
    private javax.swing.JTextField jumin_behind;
    private javax.swing.JTextField jumin_front;
    private javax.swing.JTextField log_id;
    private javax.swing.JTextField log_pwd;
    private javax.swing.JButton loginBtn;
    private javax.swing.JButton logout_btn;
    private javax.swing.JTable mainPrintTable;
    private javax.swing.JLabel main_img;
    private javax.swing.JButton main_page_Btn2;
    private javax.swing.JButton mainpageBtn;
    private javax.swing.JLabel moimTitle;
    private javax.swing.JButton moim_crea_Btn;
    private javax.swing.JButton moim_delete_Btn;
    private javax.swing.JButton moim_detail_Btn;
    private javax.swing.JTextArea moim_info;
    private javax.swing.JButton moim_join_Btn;
    private javax.swing.JTable moimlist;
    private javax.swing.JTextField moimnum_v;
    private javax.swing.JButton moinBtn;
    private javax.swing.JLabel myhobby_v;
    private javax.swing.JLabel myloc_v;
    private javax.swing.JTable mymoimList_table;
    private javax.swing.JLabel myname_v;
    private javax.swing.JButton nextBtn;
    private javax.swing.JPanel p10_mypagePanel;
    private javax.swing.JLabel p10_time_v;
    private javax.swing.JTable p11_delTable;
    private javax.swing.JTextField p11_del_v;
    private javax.swing.JButton p11_deleteBtn;
    private javax.swing.JButton p11_denyBtn;
    private javax.swing.JTextField p11_deny_v;
    private javax.swing.JTextArea p11_info;
    private javax.swing.JButton p11_infoBtn;
    private javax.swing.JButton p11_joinBtn;
    private javax.swing.JTable p11_joinTable;
    private javax.swing.JTextField p11_join_v;
    private javax.swing.JPanel p11_moimjang;
    private javax.swing.JPanel p1_login;
    private javax.swing.JPanel p2_adduser;
    private javax.swing.JPanel p3_createmoim;
    private javax.swing.JPanel p4_mainpanal;
    private javax.swing.JPanel p5_moimpage;
    private javax.swing.JPanel p6_boardPanal;
    private javax.swing.JButton p7_catBtn;
    private javax.swing.JPanel p7_chatingPanal;
    private javax.swing.JButton p7_dogBtn;
    private javax.swing.JButton p7_wolfBtn;
    private javax.swing.JPanel p8_createdocument_Panal;
    private javax.swing.JPanel p9_detailGlePanal;
    private javax.swing.JPanel pp;
    private javax.swing.JButton prevBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField search_detail_board;
    private javax.swing.JTextField searchnum;
    private javax.swing.JTextField searchword;
    private javax.swing.JLabel show_hobby;
    private javax.swing.JTextField talkBar;
    private javax.swing.JButton to_moimBtn;
    private javax.swing.JButton toboardpanalBtn;
    private javax.swing.JButton toboardpanalBtn1;
    private javax.swing.JButton toboardpanalBtn2;
    private javax.swing.JButton tochatingPanalBtn;
    private javax.swing.JButton tochatingPanalBtn1;
    private javax.swing.JButton tochatingPanalBtn2;
    private javax.swing.JButton tomainPanalBtn1;
    private javax.swing.JButton tomoiminfoBtn;
    private javax.swing.JButton tomoiminfoBtn1;
    private javax.swing.JButton tomoiminfoBtn2;
    private javax.swing.JButton tomymoimPanalBtn;
    private javax.swing.JComboBox<String> update_hobby_combobox;
    private javax.swing.JComboBox<String> update_loc_combobox;
    private javax.swing.JButton update_mypage_Btn;
    private javax.swing.JButton useMid;
    // End of variables declaration//GEN-END:variables
}
