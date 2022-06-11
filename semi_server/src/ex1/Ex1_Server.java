package ex1;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import dto.Chat;
import model.Model;

public class Ex1_Server {
	private ServerSocket ss;
	private ArrayList<ServerThread> list;
	private Model md;
	// Ex1_Server를 생성할떄 SeverSocket 클래스 생성과 포트번호를 정해줌
	// 동시에 여러 접속자에게 입출력을 해야되기에 쓰레드를 관리하기 위한 ArrayList를 생성
	// 전송받은 내용을 JDBC로 보내주기위한 Model 생성
	public Ex1_Server(int port) {
		try {
			ss = new ServerSocket(port);
			System.out.println("server start!");
			list = new ArrayList<ServerThread>();
			md = new Model();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 서버를 실행할때 수행할 명령을 위해 execute문 정의
	public void execute(){
		// while 문을 계속 명령어 수행
		// accept()를 통해 리스너와 새로운 소켓을 만들어지는것을 수행, 해당 메서드를 연결이 이루어지기전까지 대기시키는 역할을 함
		while (true) {
			try {
				Socket s = ss.accept();
				// 연결을 한 소켓의 아이피주소를 보기위한 메서드
				System.out.println(s.getInetAddress().getAddress());
				System.out.println(s.getInetAddress());
				// 스레드를 이용하기 위해 스레드를 상속받은 클래스를 정의 생성자에 연결된 소켓과 현재 메인페이지의 주소를 보냄
				ServerThread ct = new ServerThread(s,this);
				// 생성된 스레드클래스를 Arraylistdp 추가해서 관리
				list.add(ct);
				// 스레드 클래스를 start() 메서드를 이용해 실행
				ct.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// 가공된 문장을 받아 보내주는 역할을 하는 메서드 
	public void sendMsg(String str1, String str2, String str3, String str4) {
		String type1 = str1; // talk,welcome
		String type2 = str2; // usernum값
		String type3 = str3; // userid
		String type4 = str4; // msg;
		String str = "";
		// JDBC에 전달하기위해 Chat클래스 생성후 내용을 추가하고 model로 보냄
		Chat ch = new Chat();
		ch.setGroupnum(Integer.parseInt(type2.trim()));
		ch.setTalk(type4);
		md.addChat(ch);
		
		//프로토콜을 지정해서 원하는 문장으로 보냄 talk/exit/welcome
		if(type1.equals("talk")) {
			str = "["+type3+"]"+type4;
			System.out.println("Message : "+ str);
		}else if(type1.equals("exit")) {
			str= type3+"님이 퇴장하셨습니다";
		}else if(type1.equals("welcome")) {
			str= type3+"님 환영합니다.";
		}

		//모든 유저에게 완성된 str을 스트림을 통해서 보내는 작업
		for(ServerThread e : list) {
			// 해당 그룹의 일치여부를 if문을 통해 확인해서 각각의 프린트라이트 스트림을 메서드를 통해 가져와 보내줌
			if(e.getGnum().equals(type2))
			e.getPw().println(str);
		}
	}
	// 서버 실행
public static void main(String[] args) {
		new Ex1_Server(9999).execute();
	}
}	

	


