import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Austin on 5/1/2017.
 */
public class Router implements Runnable {

    int poisonReverse;
    String addr;
    String ip;
    int port;
    DV dv_original;
    int updatePeriod;

    Thread thread_advertise;
    Thread thread_rx;

    DV dv;


    public Router(String filename) {
        this.poisonReverse = 0;
        try {
            dv = new DV(new String(Files.readAllBytes(Paths.get(filename))));
            dv_original = new DV(new String(Files.readAllBytes(Paths.get(filename))));
            addr = dv.getSource();
            ip = addr.split(":")[0];
            port = Integer.valueOf(addr.split(":")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading file");
        }
        this.updatePeriod = 2;
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

        //handleInput();
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

            handleIncMsg(msg);

//            System.out.println("RECEIVED (" + IPAddress.toString()+":"+port+"): " + msg);
        }
    }

    private void handleIncMsg(String msg) {
        Scanner sc = new Scanner(msg);
        System.out.println("RECEIVED: " + msg);
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(" ");
            if(line.length != 2) {
                //System.out.println("Line does not have length 2. Length = " + line.length);
                continue;
            }
            String key = line[0];
            String weight = line[1];


        }
    }

    public void updateDV() {

    }

    public void sendUpdate() {

    }

    public void forward() {

    }

    public void advertise() throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        byte[] msg = dv.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, 9876);
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
                    //for(RouterEntry r : this.dv) System.out.println(r.toString());
                }
                default: break;
            }
        }
    }

    public void setPoisonReverse(int val) {
        this.poisonReverse = val;
    }


}
