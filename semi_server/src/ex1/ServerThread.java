package ex1;
// ���� Ŭ������ ���� ������ �ַ� ������ thread���ϰ� ������ gui�� �����Ѵ�

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
// ������ Ŭ���̾�Ʈ�� ������ �����ϱ����� �����带 ��ӹ���
	private Socket socket;
	private Ex1_Server server;
	private PrintWriter pw;
	private BufferedReader in;
	private String groupnum;
	// �����ڸ� ���� ���ϰ� ���������� �ּҸ� �������ְ� ����Ʈ ����Ʈ�� ���� true�� ���� �����÷���
	public ServerThread(Socket s, Ex1_Server ex1_Server) {
			this.socket = s;
			this.server = ex1_Server;
			try {
				pw = new PrintWriter(s.getOutputStream(),true);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	//�ش� ������ �Ҽӵ� �׷��� ��ȣ�� ���ڿ��� ��ȯ
	public String getGnum() {
		return groupnum;
	}
	//�� Ŭ���̾�Ʈ�� ���޹ޱ����� �� ������ ����Ʈ�����͸� ��ȯ���ִ� �޼���
	public PrintWriter getPw() {
		return pw;
	}
	// �����忡�� ���� �ۼ��ϱ�
	@Override

	public void run() {
		try {
			// �ش� Ŭ�󿡼� ���� �޼����� �б����� reader�� ����
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println(groupnum);
			// while���� ���� ���������� Ŭ���̾�Ʈ���� ������ �޼����� �б����� �����
			while (true) {
				String msg = in.readLine();
				System.out.println("Client Msg : "+msg);
				// �޼��� ������ ������ ���� ���� �޼����� transMsg �޼��忡 �ްԺ����� ����
				transMsg(msg);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// Ŭ���̾�Ʈ�� ���� �޼����� �����ϱ����� �޼��� 
	public void transMsg(String msg) {
		// StringTokenizer�� ���� ������ �������ݿ� ���� ����
		StringTokenizer stn = new StringTokenizer(msg,"/");
		String str1 = stn.nextToken();
		String str2 = stn.nextToken();
		// ������ Ŭ���� �׷��ȣ�� �ޱ����� ����
		if(str1.trim().equals("welcome"))groupnum = str2.trim();
		String str3 = stn.nextToken();
		String str4 = stn.nextToken();
		System.out.println("Log Token : "+str1);
		System.out.println("Log Token : "+str2);
		System.out.println("Log Token : "+str3);
		System.out.println("Log Token : "+str4);
		// ������ ������ �����ּҸ� ���� �޼��带 ȣ���� ������
		server.sendMsg(str1, str2, str3, str4);
		
	}
	
}
