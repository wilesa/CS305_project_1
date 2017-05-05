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
            addr = dv.source;
            ip = addr.split(":")[0];
            port = Integer.valueOf(addr.split(":")[1]);
            routerKey = ip.trim() + ":" + port;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading file");
        }
        this.updatePeriod = 2;

        for(String s : dv.keySet()) {
            if(!s.equals(routerKey)) {
                DV tmp = new DV();
                tmp.setSource(s);
                neighbors.put(s, tmp);
            }
        }
        for(String s : dv.keySet()) p(s+" " + dv.get(s));
        p("");
        for(String s : neighbors.keySet()) p(s);
//        updateDV();
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
        //thread_advertise.start();
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
            System.out.println("RECEIVED ("+receivePacket.getAddress()+":"+receivePacket.getPort()+")\n"+msg+"\n");
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            handleIncDV(msg);

//            System.out.println("RECEIVED (" + IPAddress.toString()+":"+port+"): " + msg);
        }
    }

    private void handleIncDV(String msg) {
        Scanner sc = new Scanner(msg);
        if(isCorrupt(msg)) return;
        DV d = new DV(msg);
        if (neighbors.containsKey(d.source)) {
            if(d.isDifferent(neighbors.get(d.source))){
                neighbors.replace(d.source, d);
                updateDV();
                try {
                    advertise();
                } catch (Exception e) {e.printStackTrace();}
            } else {
            }
        } else {
            neighbors.put(d.source, d);
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
        for(String n : neighbors.keySet()) {
            if(!reachable.contains(n)) reachable.add(n);
            for(String s : neighbors.get(n).keySet()) {
                if(!reachable.contains(s)) {
                    reachable.add(s);
                }
            }
        }

        for(String key : reachable) {
            int tmp = 0;
            int min = Integer.MAX_VALUE;
            if (original.containsKey(key)) {
                min = original.get(key);
                tempFoward.put(key, key);
            }
            for(String s : neighbors.keySet()) {
                if(neighbors.get(s).containsKey(key))  {
                    tmp = original.get(s) + neighbors.get(s).get(key);
                    if(tmp < min) {
                        min = tmp;
                        if(tempFoward.containsKey(key)) tempFoward.replace(key, s);
                        else tempFoward.put(key, s);
                    }
                }
            }
            tempDV.put(key, min);
        }
        dv = tempDV;
    }

    public void sendUpdate() {

    }

    public void forward() {

    }

    public void advertise() {
        String m = dv.toString();
        for(String s : neighbors.keySet()) {
            if(!neighbors.get(s).source.equals(dv.source)) {
                send(m, neighbors.get(s).ip, neighbors.get(s).port);
            }
        }
    }

    public void send(String msg, String ip, int port) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(ip);
            p("Sending: " + ip + ":" + port+ " from port: "+clientSocket.getLocalPort()+"\n"+msg);
            clientSocket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, IPAddress, port));
            clientSocket.close();
        } catch (Exception e) {e.printStackTrace();}
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
                    p("Current Distance Vector: ");
                    p(dv.toString());
                    p("Neighbors Distance Vectors:");
                    for(String s : neighbors.keySet())
                        if(!neighbors.get(s).keySet().isEmpty())
                            System.out.println("Source: " + s + "\n" + neighbors.get(s).toString() + "\n");
                    break;
                }
                case "AD" : {
                    advertise();
                    break;
                }

                default: break;
            }
        }
    }

    public boolean isCorrupt(String input) {
        String source = null;
        Scanner sc = new Scanner(input);
        if(!sc.hasNextLine()) return true;
        try {
            while (sc.hasNextLine()) {
                String line[];
                line = sc.nextLine().trim().split(" ");
                if (line.length != 2) throw new Exception("Line length wrong");//System.out.println("LINE LENGTH WRONG (expecting 2): " + line.toString());
                if (line[1].trim().equals("0")) source = line[0].trim();
            }
            if (source == null) {
                throw new Exception("Could not find source");//System.out.println("Could not find source");
            }
            InetAddress addr = InetAddress.getByName(source.split(":")[0].trim());
            Integer.parseInt(source.split(":")[1].trim());
            //for(String s : reachables) p(s);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public void setPoisonReverse(int val) {
        this.poisonReverse = val;
    }

    public void p(String s){
        System.out.println(s);
    }

    public static void main(String[] args) {
        Router r1 = new Router(args[0]);
        r1.run();
    }

}
