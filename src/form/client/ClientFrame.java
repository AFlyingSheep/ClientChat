package form.client;

import form.server.ServerFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.*;

/**
 * @author Shuangjian
 */
public class ClientFrame extends JFrame{
    Socket socket = null;
    PrintWriter printWriter = null;
    String username = null;
    String ipAddress = null;
    int port = 7000;

    public static void main(String[] args) {
        ClientFrame cf = new ClientFrame();
        cf.setVisible(true);
        boolean flag = true;
        while(flag){
            try{
                cf.status.setText("连接中……");
                cf.connectNow();
                flag = false;
            }catch (IOException ex){
                continue;
            }
        }
    }
    public ClientFrame() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        showMsgs = new JTextArea();
        scrollPane2 = new JScrollPane();
        sendMsgs = new JTextArea();
        sendB = new JButton();
        status = new JLabel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(showMsgs);
        }
        contentPane.add(scrollPane1);
        scrollPane1.setBounds(20, 20, 345, 295);

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(sendMsgs);
        }
        contentPane.add(scrollPane2);
        scrollPane2.setBounds(20, 325, 260, 55);

        //---- sendB ----
        sendB.setText("\u53d1\u9001");
        contentPane.add(sendB);
        sendB.setBounds(290, 325, sendB.getPreferredSize().width, 55);

        //---- status ----
        status.setText("\u672a\u8fde\u63a5");
        contentPane.add(status);
        status.setBounds(25, 400, 365, 35);

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
        initAction();
        showMsgs.setEditable(false);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTextArea showMsgs;
    private JScrollPane scrollPane2;
    private JTextArea sendMsgs;
    private JButton sendB;
    private JLabel status;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void initAction(){
        sendB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printWriter.println(username+":"+sendMsgs.getText());
                showMsgs.append(username+":"+sendMsgs.getText()+"\n");
                sendMsgs.setText("");
            }
        });
    }

    public void connectNow() throws IOException {

       /* Properties pro = new Properties();
        ClassLoader classLoader = ServerFrame.class.getClassLoader();
        URL res = classLoader.getResource("client.properties");
        String path = res.getPath();
        System.out.println(path);
        pro.load(new FileReader(path));*/

        Properties pro = new Properties();
        FileInputStream in= new FileInputStream("client.properties");
        pro.load(in);

        username = pro.getProperty("username");
        port = Integer.parseInt(pro.getProperty("port"));
        ipAddress = pro.getProperty("ip");

        socket = new Socket(ipAddress,port);
        status.setText("连接成功！");
        printWriter = new PrintWriter(socket.getOutputStream(),true);
        new OpenS(socket,showMsgs,sendMsgs).run();

    }
}


class OpenS implements Runnable{
    Socket socket;
    JTextArea show;
    JTextArea send;

    public OpenS(Socket socket,JTextArea show,JTextArea send){
        this.socket=socket;
        this.show = show;
        this.send=send;
    }

    @Override
    public void run() {
        try{
            while(true){
                InputStream in = socket.getInputStream();
                BufferedReader bfrd = new BufferedReader(new InputStreamReader(in));
                String msgs = bfrd.readLine();
                show.append("("+socket.getInetAddress()+")"+msgs+"\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}