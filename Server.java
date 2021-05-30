import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class Server {

    //quản lý các thread là các client được kết nối tới bằng socket TCP. Nhiệm vụ điều phối thông tin từ 1 client đến các client còn lại
    //server giữ kết nối của client, sử dụng hashtable để ánh xạ 1 userName với 1 luồng của client
    //Khi server muốn gửi message đến client thì server phải duyệt qua listUser để lấy luồng tương ứng để gửi dữ liệu đến cho client
    private ServerSocket server;
    public Hashtable<String, ClientConnect> listUser;
    public String dataHis;

    public Server() throws IOException {
        listUser = new Hashtable<String, ClientConnect>();
        server = new ServerSocket(2207);
        dataHis = "";
    }

    //accept client.
    private void go() throws IOException {
        while (true) {
            Socket client = server.accept();
            new ClientConnect(this, client);
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().go();
    }

    //gửi message đến client được chỉ định hoặc gửi đến tất cả client đang online.
    public void sendTo(String from, String msg, String type, String to) {
        String name = null;

        //gửi message đến tất cả client đang online
        if (to.equals("all") || to.equals("")) {
            Enumeration e = listUser.keys();
            while (e.hasMoreElements()) {
                name = (String) e.nextElement();
                listUser.get(name).sendMSG(from, msg, type);
            }

        }
        //gửi message đến client đã được chỉ định.
        else {
            Enumeration e = listUser.keys();
            while (e.hasMoreElements()) {
                name = (String) e.nextElement();
                if (name.equals(to) || name.equals(from)) {
                    listUser.get(name).sendMSG(from, msg, type);
                }
            }

        }
    }
}
