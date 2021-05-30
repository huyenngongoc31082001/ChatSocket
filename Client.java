import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



////Khởi tạo giao diện, phân tích, xử lý, gửi mọi yêu cầu đến Máy chủ (Server).
public class Client extends JFrame implements ActionListener {
    private JButton send, clear, login;
    private JPanel p_login, p_chat;
    private JTextField nick, nick1, message;
    private JTextArea msg, online;
    private JTextField to;

    private Socket client;
    private DataStream dataStream;
    public DataOutputStream dos;
    public DataInputStream dis;
    public String username;
    public boolean canLogin;


    public Client() {
        super("Chat");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dataStream.stopThread();
                exit();
            }
        });
        setSize(800, 400);
        addItem();
        setVisible(true);
        canLogin = false;
    }


    //tạo giao diện
    private void addItem() {
        //button gửi
        send = new JButton("Gửi");
        send.addActionListener(this);

        //button xóa tin nhắn
        clear = new JButton("Xóa");
        clear.addActionListener(this);

        //button đăng nhập
        login = new JButton("Đăng nhập");
        login.addActionListener(this);

        p_chat = new JPanel();
        p_chat.setLayout(new BorderLayout());

        //panel đăng nhập
        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        nick = new JTextField(20);
        p1.add(new JLabel("Nick name: "));
        p1.add(nick);

        //panel chat room
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        //tạo và thêm panel online và panel chat room
        JPanel p22 = new JPanel();
        p22.setLayout(new FlowLayout(FlowLayout.CENTER));
        p22.add(new JLabel("Danh sách online"));
        p2.add(p22, BorderLayout.NORTH);

        online = new JTextArea(10, 10);
        online.setEditable(false);
        p2.add(new JScrollPane(online), BorderLayout.CENTER);
        p2.add(new JLabel("     "), BorderLayout.SOUTH);
        p2.add(new JLabel("     "), BorderLayout.EAST);
        p2.add(new JLabel("     "), BorderLayout.WEST);

        msg = new JTextArea(10, 20);
        msg.setEditable(false);

        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        p3.add(new JLabel("Tin nhắn"));
        message = new JTextField(30);
        p3.add(message);
        p3.add(send);
        p3.add(clear);
        p3.add(new JLabel("Đến"));
        to = new JTextField(13);
        p3.add(to);

        p_chat.add(new JScrollPane(msg), BorderLayout.CENTER);
        p_chat.add(p1, BorderLayout.NORTH);
        p_chat.add(p2, BorderLayout.EAST);
        p_chat.add(p3, BorderLayout.SOUTH);
        p_chat.add(new JLabel("     "), BorderLayout.WEST);

        p_chat.setVisible(false);
        add(p_chat, BorderLayout.CENTER);
        //-------------------------
        p_login = new JPanel();
        p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
        p_login.add(new JLabel("Nick : "));
        nick1 = new JTextField(20);
        p_login.add(nick1);
        p_login.add(login);

        add(p_login, BorderLayout.NORTH);
    }

    //go () mở kết nối đến máy chủ trên "localhost" của cổng 2207 và xác định 2 đầu vào / đầu ra của loại dữ liệu (dis, dos)
    private void go() {
        try {
            client = new Socket("localhost", 2207);
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối, xem lại dây mạng đi hoặc room chưa mở.", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Client().go();
    }


    // thoát khỏi server .
    private void exit() {
        try {
            sendMSG(username, "", "logout", "all");
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.exit(0);
    }

    public void getMSG(String name, String data, String type) {
        if (type.equals("nomal")) {
            if (name.equals(username)) {
                data = "Tôi: " + data;
            } else {
                data = name + ": " + data;
            }
            this.msg.append(data);
        } else if (type.equals("login")) {
            if (name.equals(username)) {
                if (!data.equals("0")){
                    canLogin = true;
                    this.online.setText(data);
                }
            } else {
                this.online.setText(data);
            }
        } else {
            this.online.setText(data);

        }
    }

    private void sendMSG(String from, String data, String type, String to) {
        sendMSG(from);
        sendMSG(data);
        sendMSG(type);
        sendMSG(to);
    }

    private void sendMSG(String data) {
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //xử lý các nút điều khiển
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clear) {
            message.setText("");
        } else if (e.getSource() == send) {
            String toClient = to.getText();
            if (message.getText() != "") {
                String mes = message.getText();
                if (toClient.equals("all") || toClient.equals("")) {
                    mes += "";
                } else {
                    mes += " (private to ";
                    mes += toClient;
                    mes += ") ";
                }
                mes += '\n';
                sendMSG(username, mes, "nomal", toClient);
            }
            message.setText("");
        } else if (e.getSource() == login) {
            username = nick1.getText();
            //System.out.println(username);
            sendMSG(username, "", "login", "all");
            //System.out.println("username " + username + "  ");
            try {
                String s1, s2, s3;
                s1 = dis.readUTF();
                s2 = dis.readUTF();
                s3 = dis.readUTF();
                getMSG(s1, s2, s3);
                //System.out.println("get msg from server nickname " + s1 + " data " + s2 + " to " + s3);

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (canLogin) {
                p_chat.setVisible(true);
                p_login.setVisible(false);
                nick.setText(username);
                nick.setEditable(false);
                this.setTitle(username);
                msg.append("Đã đăng nhập thành công\n");
                dataStream = new DataStream(this, this.dis);
            } else {
                //System.out.println("khong thể đn");
                JOptionPane.showMessageDialog(this, "Đã tồn tại níck này trong room, bạn vui lòng nhập lại.", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
