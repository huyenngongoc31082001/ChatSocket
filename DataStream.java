import java.io.DataInputStream;
import java.io.IOException;

//tạo luồng nhận dự liệu cho client
//tự khởi động Thread.  Khi đối tượng (hoặc lớp) này được khởi tạo, nó sẽ tự động khởi động và lắng nghe Máy chủ thông qua DataInputStream.
//Đọc 3 message và gửi đến client qua phương thức
//Client: client. getMSG(msg1, msg2, msg3)
//Phương thức stopThread () được sử dụng để phá vỡ vòng lặp vô hạn while(true).
public class DataStream extends Thread {
    public boolean run;
    private DataInputStream dis;
    private Client client;

    public DataStream(Client client, DataInputStream dis) {
        run = true;
        this.client = client;
        this.dis = dis;
        this.start(); //startThread
    }

    //thread body
    public void run() {
        String msg1, msg2, msg3;
        while (run) {
            try {
                msg1 = dis.readUTF();////gửi đi dưới dạng encode UTF(để tại client có thể hiện ra các kí tự chuẩn dạng UTF)
                msg2 = dis.readUTF();
                msg3 = dis.readUTF();
                client.getMSG(msg1, msg2, msg3);
            } catch (IOException e) {
                try {
                    dis.close();
                } catch (IOException ex) {
                }

            }
        }
        try {
            dis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // phá vỡ vòng lặp vô hạn while(run)
    public void stopThread() {
        run = false;
    }
}
