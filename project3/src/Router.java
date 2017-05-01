import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    ArrayList<RouterEntry> dv_original;
    ArrayList<RouterEntry> dv;
    ArrayList<ArrayList<RouterEntry>> dv_neighbors;
    byte[] dv_bytes;
    int updatePeriod;

    Thread thread_advertise;
    Thread thread_rx;


    public Router(String filename) {
        this.poisonReverse = 0;
        this.dv = readFile(filename);
        this.dv_original = dv;
        this.dv_neighbors = new ArrayList<>();
        this.ipaddr = dv.get(0).getIP();
        this.port = dv.get(0).getPort();

        this.updatePeriod = 2;

        for(RouterEntry r : dv) System.out.println(r.toString());

        updateDV();
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
                try {
                    rx();
                } catch (Exception e) {e.printStackTrace();}

            }
        });
        thread_advertise.start();
        thread_rx.start();

        handleInput();
    }

    public Boolean ifChanged() {
        return false;
    }


    public void rx() throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(this.port);
        byte[] receiveData = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String msg = new String(receivePacket.getData());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            Scanner sc = new Scanner(msg);
            while(sc.hasNextLine()){

            }

//            System.out.println("RECEIVED (" + IPAddress.toString()+":"+port+"): " + msg);
        }
    }

    public void updateDV() {

        //Converts Array of RouterEntry to byte array
        String s = "";
        for(RouterEntry e : this.dv) {
            s = s + e.toString() + "\n";
        }
        this.dv_bytes = s.getBytes();
    }

    public void sendUpdate() {

    }

    public void forward() {

    }

    public void advertise() throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
//        byte[] msg = arrayToBytes();//"advertise".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(dv_bytes, dv_bytes.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        clientSocket.close();
    }

    public void handleInput() {
        System.out.println("Type 'help' for list of commands.");
        Scanner sc = new Scanner(System.in);
        String input;
        while(true) {
            System.out.print("-> ");
            input = sc.nextLine();
            switch(input) {
                case "help" : System.out.println(input);break;
                case "PRINT": {
                    for(RouterEntry r : this.dv) System.out.println(r.toString());
                }
                default: break;
            }
        }
    }

    public void setPoisonReverse(int val) {
        this.poisonReverse = val;
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

}
