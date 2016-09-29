package com.yumaolin.util.Socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService {
	class MyServerReader implements Runnable{
		private DataInputStream dataInput;
		
		public MyServerReader(DataInputStream dataInput){
			this.dataInput = dataInput;
		}
		
		public void run(){
			String info;
			try {
				while(true){
					info = dataInput.readUTF();
					System.out.println("客户端 :"+info);
					if("bye".equals(info)){
						System.out.println("对方下线，程序退出!");
						System.exit(0);
					}
				}
			}catch(Exception e){
				if(e.toString().contains("java.net.SocketException: Connection reset")){
					System.out.println("对方下线，程序退出!");
					System.exit(0);
				}
			}
		}
	}
	
	class MyServerWrite implements Runnable{
		private DataOutputStream data;
		public MyServerWrite (DataOutputStream data){
			this.data = data;
		}
		
		public void run(){
			try {
				//读取键盘输入流
				InputStreamReader isr = new InputStreamReader(System.in);
				//封装键盘输入流
				BufferedReader br = new BufferedReader(isr);
				while(true){
					String thisInfo = br.readLine();
					data.writeUTF(thisInfo);
					if("bye".equals(thisInfo)){
						System.out.println("对方下线，程序退出!");
						System.exit(0);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	class MyServiceSocket implements Runnable{
		private Socket socket;
		public MyServiceSocket(Socket socket){
			this.socket = socket;
		}
		
		public void run(){
			try{
				OutputStream output = socket.getOutputStream();//获取输出流
				DataOutputStream data = new DataOutputStream(output);
				 //打开输入流
			     InputStream is = socket.getInputStream();
				//封装输入流
				DataInputStream dis = new DataInputStream(is);
				Thread t1 = new Thread(new SocketService().new MyServerReader(dis));
				t1.setDaemon(true);
				Thread t2 = new Thread(new SocketService().new MyServerWrite(data));
				t2.setDaemon(true);
				t1.start();
				t2.start();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
  public static void main(String[] args){
	  try {
		ServerSocket service = new ServerSocket(8090);//指定连接端口
		while(true){
			Socket socket = service.accept();//获取连接
			socket.setKeepAlive(true);
			new Thread(new SocketService().new MyServiceSocket(socket)).start();
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
}
