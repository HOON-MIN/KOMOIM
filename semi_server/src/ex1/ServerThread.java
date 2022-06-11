package ex1;
// 내부 클래스로 만들어도 되지만 주로 서버는 thread로하고 관리를 gui에 연결한다

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ServerThread extends Thread{
// 각각의 클라이언트의 소켓을 관리하기위해 쓰레드를 상속받음
	private Socket socket;
	private Ex1_Server server;
	private PrintWriter pw;
	private BufferedReader in;
	private String groupnum;
	// 생성자를 통해 소켓과 메인페이지 주소를 정의해주고 프린트 라이트를 생성 true를 통해 오토플러시
	public ServerThread(Socket s, Ex1_Server ex1_Server) {
			this.socket = s;
			this.server = ex1_Server;
			try {
				pw = new PrintWriter(s.getOutputStream(),true);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	//해당 소켓이 소속된 그룹의 번호를 문자열로 반환
	public String getGnum() {
		return groupnum;
	}
	//각 클라이언트로 전달받기위해 각 소켓의 프린트라이터를 반환해주는 메서드
	public PrintWriter getPw() {
		return pw;
	}
	// 스레드에게 할일 작성하기
	@Override

	public void run() {
		try {
			// 해당 클라에서 보낸 메세지를 읽기위해 reader를 생성
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println(groupnum);
			// while문을 통해 지속적으로 클라이언트에서 보내는 메세지를 읽기위해 대기중
			while (true) {
				String msg = in.readLine();
				System.out.println("Client Msg : "+msg);
				// 메세지 가공과 전달을 위해 받은 메세지를 transMsg 메서드에 메게변수로 전달
				transMsg(msg);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// 클라이언트에 받은 메세지를 가공하기위한 메서드 
	public void transMsg(String msg) {
		// StringTokenizer를 통해 문장을 프로토콜에 맞춰 가공
		StringTokenizer stn = new StringTokenizer(msg,"/");
		String str1 = stn.nextToken();
		String str2 = stn.nextToken();
		// 접속한 클라의 그룹번호를 받기위한 문장
		if(str1.trim().equals("welcome"))groupnum = str2.trim();
		String str3 = stn.nextToken();
		String str4 = stn.nextToken();
		System.out.println("Log Token : "+str1);
		System.out.println("Log Token : "+str2);
		System.out.println("Log Token : "+str3);
		System.out.println("Log Token : "+str4);
		// 가공한 문장을 메인주소를 통해 메서드를 호출해 보내줌
		server.sendMsg(str1, str2, str3, str4);
		
	}
	
}
