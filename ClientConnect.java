import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;

public class ClientConnect extends Thread {
    public Socket socket;
    public Server server;
    private String nickName;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean run;

    //Constructor: khởi tạo
    public ClientConnect(Server server, Socket client) throws IOException {
        this.server = server;
        this.socket = client;
        dos = new DataOutputStream(client.getOutputStream());
        dis = new DataInputStream(client.getInputStream());
        run = true;
        this.start(); //SELF starting thread
    }


    //Thread body
    public void run() {
        while (run) {
            String name, data, type, to;
            while (run) {
                name = getMSG(); // lấy tên client
                data = getMSG(); // lấy dữ liệu
                type = getMSG(); // lấy loại dữ liêu login/logout
                to = getMSG(); //lấy dữ liệu để kiểm tra private chat or all

                //System.out.println("get msg from client username " + name + " data " + data + " type " + type + " to " + to);
                if (type.equals("login")) {
                    nickName = name;
                    boolean cannotLogin = server.listUser.containsKey(nickName);
                    if (!cannotLogin) {
                        server.listUser.put(nickName, this);
                        Enumeration e = server.listUser.keys();
                        while (e.hasMoreElements()) {
                            data += (String) e.nextElement();
                            data += "\n";
                        }
                        server.sendTo(name, data, type, to);
                    } else {
                        data = "0";
                        sendMSG(nickName);
                        sendMSG(data);
                        sendMSG("cannotLogin");

                    }
                } else if (type.equals("logout")) {
                    server.listUser.remove(name);
                    Enumeration e = server.listUser.keys();
                    while (e.hasMoreElements()) {
                        data += (String) e.nextElement();
                        data += "\n";
                    }
                    logout();
                    server.sendTo(name, data, type, to);
                } else {
                    server.sendTo(name, data, type, to);
                }
                //System.out.println("nickname " + nickName + " data " + data + " type " + type + " to " + to);
            }
        }
    }


    // nhận dữ liệu
    private String getMSG() {
        String data = null;
        try {
            data = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    //Gửi dữ liệu đến Client
    private void sendMSG(String data) {
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Gửi dữ liệu đến Client thông qua void sendMSG(String data)
    public void sendMSG(String msg1, String msg2, String msg3) {
        sendMSG(msg1);
        sendMSG(msg2);
        sendMSG(msg3);
    }

    //thoát khỏi server.
    private void logout() {
        try {
            run = false;
            dos.close();
            dis.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
