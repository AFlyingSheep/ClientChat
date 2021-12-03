package com;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket socket = null;
    public Client(){
        startClient("127.0.0.1",6866);
    }

    public void startClient(String ipAdress,int port){
        try{
            socket = new Socket(ipAdress,port);
            System.out.println("连接服务器成功！");
            new  Thread(this).start();

            OutputStream os= socket.getOutputStream();
            PrintStream ps=new PrintStream(os);
            while(true){
                Scanner scanner = new Scanner(System.in);
                String sendMsgs = scanner.next();
                ps.println(sendMsgs);
            }


        }
        catch (IOException exception) {
            System.out.println("连接失败，请重试");
        }
    }

    @Override
    public void run() {
        try{
            while(true){
                InputStream in = socket.getInputStream();
                BufferedReader bfrd = new BufferedReader(new InputStreamReader(in));
                String msgs = bfrd.readLine();
                System.out.println(socket.getInetAddress() + ":" + msgs);
            }
        } catch (IOException exception) {
            System.out.println("连接异常，即将退出！");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
