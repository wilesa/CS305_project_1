import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Austin on 5/1/2017.
 */
public class Router implements Runnable {

    int poisonReverse;
    String ipaddr;
    int port;
    ArrayList<RouterEntry> dv;
    int updatePeriod;

    Thread thread_advertise;
    Thread thread_rx;

    DatagramSocket sock;



    public Router(String filename) {
        this.poisonReverse = 0;
        this.dv = readFile(filename);
        this.ipaddr = dv.get(0).getIP();
        this.port = dv.get(0).getPort();

        this.updatePeriod = 2;

        for(RouterEntry r : dv) System.out.println(r.toString());



    }

    @Override
    public void run() {
        thread_advertise = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        advertise();
                        Thread.sleep(updatePeriod * 1000);
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        });

        thread_rx = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });


        thread_advertise.start();
    }

    public Boolean ifChanged() {
        return false;
    }

    public ArrayList<RouterEntry> readFile(String filename) {
        ArrayList<RouterEntry> res = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(filename));
            if(!sc.hasNextLine()) return null;
            String[] line = sc.nextLine().split(" ");
            res.add(new RouterEntry(line[0], line[1], 0));
            while (sc.hasNextLine()) {
                line = sc.nextLine().split(" ");
                if(line.length != 3) throw new Exception("Line expecting length of 3: length of line is " + line.length);
                res.add(new RouterEntry(line[0], line[1], line[2]));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading file");
        }
        return null;
    }

    public void rx() {

    }

    public void updateDV() {

    }

    public void sendUpdate() {

    }

    public void forward() {

    }

    public void setPoisonReverse() {

    }

    public void advertise() throws Exception {

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        String sentence = "advertise";
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//        clientSocket.receive(receivePacket);
//        String modifiedSentence =
//                new String(receivePacket.getData());
//        System.out.println("FROM SERVER: " +
//                modifiedSentence);
//        clientSocket.close();
    }




}
