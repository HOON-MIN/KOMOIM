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
	// Ex1_Server�� �����ҋ� SeverSocket Ŭ���� ������ ��Ʈ��ȣ�� ������
	// ���ÿ� ���� �����ڿ��� ������� �ؾߵǱ⿡ �����带 �����ϱ� ���� ArrayList�� ����
	// ���۹��� ������ JDBC�� �����ֱ����� Model ����
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
	// ������ �����Ҷ� ������ ����� ���� execute�� ����
	public void execute(){
		// while ���� ��� ��ɾ� ����
		// accept()�� ���� �����ʿ� ���ο� ������ ��������°��� ����, �ش� �޼��带 ������ �̷������������ ����Ű�� ������ ��
		while (true) {
			try {
				Socket s = ss.accept();
				// ������ �� ������ �������ּҸ� �������� �޼���
				System.out.println(s.getInetAddress().getAddress());
				System.out.println(s.getInetAddress());
				// �����带 �̿��ϱ� ���� �����带 ��ӹ��� Ŭ������ ���� �����ڿ� ����� ���ϰ� ���� ������������ �ּҸ� ����
				ServerThread ct = new ServerThread(s,this);
				// ������ ������Ŭ������ Arraylistdp �߰��ؼ� ����
				list.add(ct);
				// ������ Ŭ������ start() �޼��带 �̿��� ����
				ct.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ������ ������ �޾� �����ִ� ������ �ϴ� �޼��� 
	public void sendMsg(String str1, String str2, String str3, String str4) {
		String type1 = str1; // talk,welcome
		String type2 = str2; // usernum��
		String type3 = str3; // userid
		String type4 = str4; // msg;
		String str = "";
		// JDBC�� �����ϱ����� ChatŬ���� ������ ������ �߰��ϰ� model�� ����
		Chat ch = new Chat();
		ch.setGroupnum(Integer.parseInt(type2.trim()));
		ch.setTalk(type4);
		md.addChat(ch);
		
		//���������� �����ؼ� ���ϴ� �������� ���� talk/exit/welcome
		if(type1.equals("talk")) {
			str = "["+type3+"]"+type4;
			System.out.println("Message : "+ str);
		}else if(type1.equals("exit")) {
			str= type3+"���� �����ϼ̽��ϴ�";
		}else if(type1.equals("welcome")) {
			str= type3+"�� ȯ���մϴ�.";
		}

		//��� �������� �ϼ��� str�� ��Ʈ���� ���ؼ� ������ �۾�
		for(ServerThread e : list) {
			// �ش� �׷��� ��ġ���θ� if���� ���� Ȯ���ؼ� ������ ����Ʈ����Ʈ ��Ʈ���� �޼��带 ���� ������ ������
			if(e.getGnum().equals(type2))
			e.getPw().println(str);
		}
	}
	// ���� ����
public static void main(String[] args) {
		new Ex1_Server(9999).execute();
	}
}	

	


