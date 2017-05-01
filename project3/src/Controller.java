import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Austin on 5/1/2017.
 */
public class Controller {

    public static void main(String[] args) throws Exception {
        Router r1 = new Router("r1.txt");
        r1.run();
    }

}
