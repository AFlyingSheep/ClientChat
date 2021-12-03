package form.server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;


/**
 * @author Shuangjian
 */
public class ServerFrame extends JFrame implements Runnable{
    public static void main(String[] args) throws IOException {
        ServerFrame sf=new ServerFrame();
        sf.setVisible(true);
        sf.run();


    }
    ServerSocket serverSocket = null;
    OutputStream os = null;
    String username = null;
    int port = 7000;
    Socket socket = null;
    public static int onlineNum = 0;

    public ServerFrame() throws IOException {
        initComponents();
        initActionListener();
        initCom();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        sendB = new JButton();
        label1 = new JLabel();
        status = new JLabel();
        scrollPane1 = new JScrollPane();
        showMsgs = new JTextArea();
        scrollPane2 = new JScrollPane();
        sendMsgs = new JTextArea();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- sendB ----
        sendB.setText("\u53d1\u9001");
        contentPane.add(sendB);
        sendB.setBounds(295, 325, 70, 75);

        //---- label1 ----
        label1.setText("\u72b6\u6001\uff1a");
        contentPane.add(label1);
        label1.setBounds(25, 420, 45, label1.getPreferredSize().height);

        //---- status ----
        status.setText("\u672a\u8fde\u63a5");
        contentPane.add(status);
        status.setBounds(60, 420, 320, status.getPreferredSize().height);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(showMsgs);
        }
        contentPane.add(scrollPane1);
        scrollPane1.setBounds(25, 15, 335, 300);

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(sendMsgs);
        }
        contentPane.add(scrollPane2);
        scrollPane2.setBounds(25, 330, 265, 70);

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton sendB;
    private JLabel label1;
    private JLabel status;
    private JScrollPane scrollPane1;
    private JTextArea showMsgs;
    private JScrollPane scrollPane2;
    private JTextArea sendMsgs;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private void initCom(){
       sendB.setEnabled(false);
       showMsgs.setEditable(false);
       sendMsgs.setEditable(false);
    }

    private void initActionListener(){
        sendB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrintWriter printWriter = null;
                try {
                    printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.println(username+":"+sendMsgs.getText());
                    showMsgs.append(username+":"+sendMsgs.getText()+"\n");
                    sendMsgs.setText("");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run(){
        try {
            Properties pro = new Properties();
            ClassLoader classLoader = ServerFrame.class.getClassLoader();
            URL res = classLoader.getResource("server.properties");
            String path = res.getPath();
            System.out.println(path);
            pro.load(new FileReader(path));

            /*Properties pro = new Properties();
            FileInputStream in= new FileInputStream("server.properties");
            pro.load(in);*/



            username = pro.getProperty("username");
            port = Integer.parseInt(pro.getProperty("port"));

            status.setText("等待连接……");
            serverSocket = new ServerSocket(port);

            while(true){
                socket = serverSocket.accept();
                status.setText("开启成功！");
                sendB.setEnabled(true);
                sendMsgs.setEditable(true);
                new recieveThread(socket,showMsgs).start();
                onlineNum++;
                showMsgs.append(socket.getInetAddress()+"进入了房间！\n");
                showMsgs.append("在线人数:"+onlineNum+"\n");
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

class recieveThread extends Thread{
    Socket socket;
    JTextArea jTextArea;
    recieveThread(Socket socket,JTextArea jTextArea){
        this.jTextArea = jTextArea;
        this.socket = socket;
    }
    @Override
    public void run() {
        try{
            while(true){
                InputStream in =socket.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                String msgs = bf.readLine();
                jTextArea.append("("+socket.getInetAddress()+")"+msgs+"\n");
            }
        } catch (IOException exception) {
            jTextArea.append("客户端断开连接！");
            ServerFrame.onlineNum--;
        }
    }
}