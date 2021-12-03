package com;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable{
    private Socket socket =null ;

    public static void main(String[] args) {
        Server server = new Server();
        server.startChat();
    }

    public void startChat() {
        try{
            ServerSocket serverSocket = new ServerSocket(6866);
            socket  = serverSocket.accept();
            System.out.println("连接成功！");
            new Thread(this).start();

            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            while(true){
                Scanner scanner = new Scanner(System.in);
                String sendMsgs = scanner.next();
                ps.println(sendMsgs);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run(){
        try{
            while(true){
                InputStream in = socket.getInputStream();
                BufferedReader bfrd = new BufferedReader(new InputStreamReader(in));
                String msgs = bfrd.readLine();
                System.out.println(socket.getInetAddress()+":"+msgs);
            }
        } catch (IOException exception) {
            System.out.println("连接异常，即将退出！");
        }
    }

}