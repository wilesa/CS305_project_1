import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

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
    HashMap<String, RouterEntry> routerMap;
    ArrayList<String> neighbors;
    ArrayList<String> reachable;
    byte[] dv_bytes;
    int updatePeriod;

    Thread thread_advertise;
    Thread thread_rx;


    public Router(String filename) {
        this.routerMap = new HashMap<>();
        this.poisonReverse = 0;
        init(filename);
        this.dv_neighbors = new ArrayList<>();
        //this.ipaddr = dv.get(0).getIP();
        //this.port = dv.get(0).getPort();

        this.updatePeriod = 2;

        ArrayList<String> neighbors = new ArrayList<String>(this.routerMap.keySet());
//        Set set = routerMap.entrySet();
//        Iterator i = set.iterator();
//        while(i.hasNext()) {
//            Map.Entry me = (Map.Entry)i.next();
//            RouterEntry ra = (RouterEntry) me.getValue();
//            System.out.println(me.getKey()+": "+ra.getWeight());
//        }
        for(String s : reachable) System.out.println(routerMap.get(s).toString());

        updateDV();
    }

    private void init(String filename) {
        //ArrayList<RouterEntry> res = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(filename));
            if(!sc.hasNextLine()) return;
            String[] line = sc.nextLine().split(" ");
            this.ipaddr = line[0];
            this.port = Integer.valueOf(line[1]);
            routerMap.put(line[0] + ":" + line[1], new RouterEntry(line[0], line[1], 0));
            while (sc.hasNextLine()) {
                line = sc.nextLine().split(" ");
                if(line.length != 3) throw new Exception("Line expecting length of 3: length of line is " + line.length);
                routerMap.put(line[0]+":"+line[1], new RouterEntry(line[0], line[1], line[2]));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading file");
        }
        this.neighbors = new ArrayList<String>(this.routerMap.keySet());
        this.reachable = new ArrayList<String>(this.routerMap.keySet());
        //this.dv = res;
        //this.dv_original = res;

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

            System.out.println("RECEIVED (" + IPAddress.toString()+":"+port+"): " + msg);
        }
    }

    private void handleIncMsg(String msg) {
        Scanner sc = new Scanner(msg);
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(" ");
            String key = line[0];
            String weight = line[1];


        }
    }

    public void updateDV() {

        this.reachable = new ArrayList<String>(this.routerMap.keySet());
        //Converts Array of RouterEntry to byte array
        String s = "";
        for(String router : this.reachable) {
            s = s + this.routerMap.get(router).toString() + "\n";
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

    public void readFile(String filename) {

    }

}
