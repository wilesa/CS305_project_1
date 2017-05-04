import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by Austin on 5/1/2017.
 */
public class Controller {

    public static void main(String[] args) throws Exception {
        Router r1 = new Router("r1.txt");
        Thread t1 = new Thread(r1);
        t1.start();

    }

    public static void p(String s){System.out.println(s);}

}
