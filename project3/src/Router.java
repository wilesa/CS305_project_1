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
    String routerKey;
    int port;
    DV original;
    int updatePeriod;
    HashMap<String, DV> neighbors;
    HashMap<String, String> forward;

    Thread thread_advertise;
    Thread thread_rx;

    DV dv;


    public Router(String filename) {
        this.neighbors = new HashMap<>();
        this.forward = new HashMap<>();
        this.poisonReverse = 0;
        try {
            dv = new DV(new String(Files.readAllBytes(Paths.get(filename))));
            original = new DV(new String(Files.readAllBytes(Paths.get(filename))));
            addr = dv.getSource();
            ip = addr.split(":")[0];
            port = Integer.valueOf(addr.split(":")[1]);
            routerKey = ip.trim() + ":" + port;
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

            handleIncDV(msg);

//            System.out.println("RECEIVED (" + IPAddress.toString()+":"+port+"): " + msg);
        }
    }

    private void handleIncDV(String msg) {
        Scanner sc = new Scanner(msg);
        DV d = new DV(msg);
        System.out.println("RECEIVED ("+d.getSource()+"): \n" + d.toString());
        if (neighbors.containsKey(d.getSource().trim())) {
            p("Check for updated DV");
            if(d.isDifferent(neighbors.get(d.getSource().trim()))){
                neighbors.replace(d.getSource().trim(), d);
                p("Calculating new DV");
                updateDV();
                try {
                    advertise();
                } catch (Exception e) {e.printStackTrace();}
            } else {
                p("DV has not changed");
            }
        } else {
            p("DV from new neighbor! Adding to map");
            neighbors.put(d.getSource(),d);
            p("Calculating new DV");
            updateDV();
            try {
                advertise();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void updateDV() {
        DV tempDV= new DV();
        tempDV.setSource(routerKey);
        HashMap<String,String> tempFoward = new HashMap<>();
        ArrayList<String> reachable = new ArrayList<>();
        //HashMap<String, RouterEntry> all = new HashMap<>();
        for(String n : neighbors.keySet()) {
            for(String s : neighbors.get(n).getReachables()) {
                if(!reachable.contains(s)) reachable.add(s);
            }
        }

        for(String key : reachable) {
            int tmp = 0;
            int min = Integer.MAX_VALUE;
            if (original.containsKey(key)) {
                min = original.get(key).getWeight();
                tempFoward.put(key, key);
            }
            for(String s : neighbors.keySet()) {
                if(neighbors.get(s).containsKey(key))  {
                    tmp = original.get(s).getWeight() + neighbors.get(s).get(key).getWeight();
                    if(tmp < min) {
                        min = tmp;
                        if(tempFoward.containsKey(key)) tempFoward.replace(key, s);
                        else tempFoward.put(key, s);
                    }
                }
            }
            tempDV.put(key, new RouterEntry(key, "" + min));
        }
    }

    public void sendUpdate() {

    }

    public void forward() {

    }

    public void advertise() throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
        byte[] msg = dv.toString().getBytes();
        //DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, 9876);
        clientSocket.send(new DatagramPacket(msg, msg.length, IPAddress, 9876));
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

    public void p(String s){
        System.out.println(s);
    }

}
