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

    boolean poisonReverse;
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
        this.poisonReverse = false;
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
            //System.out.println("RECEIVED ("+receivePacket.getAddress()+":"+receivePacket.getPort()+")\n"+msg+"\n");
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            Scanner sc = new Scanner(msg);
            if (sc.nextLine().contains("[")) handleIncMsg(msg);
            else handleIncDV(msg);
            receiveData = new byte[1024];
//            System.out.println("RECEIVED (" + IPAddress.toString()+":"+port+"): " + msg);
        }
//        serverSocket.close();
    }

    private void handleIncMsg(String msg) {
        Scanner sc = new Scanner(new Scanner(msg).nextLine());
        sc.next();
        String fromIP = sc.next();
        String fromPort = sc.next();
        sc.next();
        String toIP = sc.next();
        int toPort = Integer.parseInt(sc.next().trim());
        if(toIP.equals(this.ip) && toPort==this.port) {
            p("OK SHIT WE JUST REVEIVED A MESSAGE");
            p(msg);
        } else forward(msg);
    }

    private void handleIncDV(String msg) {
        Scanner sc = new Scanner(msg);
        if(isCorrupt(msg)) return;
        DV d = new DV(msg);
        if (neighbors.containsKey(d.source)) {
            if(d.isDifferent(neighbors.get(d.source))){
                neighbors.replace(d.source, d);
                updateDV();
//                try {
//                    advertise();
//                } catch (Exception e) {e.printStackTrace();}
            } else {
            }
        } else {
            neighbors.put(d.source, d);
            updateDV();
//            try {
//                advertise();
//            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void updateDV() {
//        boolean changed = false;
        DV tempDV= new DV();
        tempDV.setSource(routerKey);
        HashMap<String,String> tempForward = new HashMap<>();
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
                tempForward.put(key, key);
            }
            for(String s : neighbors.keySet()) {
                if(neighbors.get(s).containsKey(key))  {
                    if(neighbors.get(s).get(key) != Integer.MAX_VALUE) {
                        tmp = original.get(s) + neighbors.get(s).get(key);
                        if (tmp < min) {
                            min = tmp;
                            if (tempForward.containsKey(key)) tempForward.replace(key, s);
                            else tempForward.put(key, s);
                        }
                    }
                }
            }
            tempDV.put(key, min);
        }
        forward = tempForward;
        if(dv.isDifferent(tempDV)){
            dv = tempDV;
            System.out.println("new dv calculated");
            for(String s : dv.keySet()) {
                System.out.println(s + " " + dv.get(s) + " " + forward.get(s));
            }
            try {
                advertise();
            } catch (Exception e) {e.printStackTrace();}
        }
        dv = tempDV;

    }

    public void sendUpdate() {

    }

    public void advertise() {
        String m;
        for(String s : neighbors.keySet()) {
            if(!neighbors.get(s).source.equals(dv.source)) {
                if(poisonReverse) m = dv.getStringPR(s, this.forward);
                else m = dv.toString();
                sendDV(m, neighbors.get(s).ip, neighbors.get(s).port);
            }
        }
    }

    public void sendDV(String msg, String ip, int port) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();//this.port);
            InetAddress IPAddress = InetAddress.getByName(ip);
            //p("Sending: " + ip + ":" + port+ " from port: "+clientSocket.getLocalPort()+"\n"+msg);
            clientSocket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, IPAddress, port));
            clientSocket.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void send(String msg, String ip, int port) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();//this.port);
            String forwardIP = this.forward.get(ip+":"+port).split(":")[0].trim();
            int forwardPort = Integer.parseInt(this.forward.get(ip+":"+port).split(":")[1].trim());
            InetAddress IPAddress = InetAddress.getByName(forwardIP);
            String newMsg = "[from " + this.ip + " " + this.port + " to " + ip + " " + port +" ]\n" + msg + "\n" + this.ip + ":" + this.port;
            p("Sending: " + ip + ":" + port+ " from port: "+clientSocket.getLocalPort()+"\n"+newMsg + " through " + forwardIP+":"+forwardPort);
            clientSocket.send(new DatagramPacket(newMsg.getBytes(), newMsg.getBytes().length, IPAddress, forwardPort));
            clientSocket.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    public void forward(String msg) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();//this.port);
            Scanner sc = new Scanner(new Scanner(msg).nextLine());
            sc.next();
            String fromIP = sc.next();
            String fromPort = sc.next();
            sc.next();
            String toIP = sc.next();
            int toPort = Integer.parseInt(sc.next().trim());
            if(!this.forward.containsKey(toIP+":"+toPort)) return;
            String forwardIP = this.forward.get(toIP+":"+toPort).split(":")[0].trim();
            int forwardPort = Integer.parseInt(this.forward.get(toIP+":"+toPort).split(":")[1].trim());
            String newMsg = msg + " " + this.ip + ":" + this.port;
            InetAddress IPAddress = InetAddress.getByName(forwardIP);


            p("Forwarding! Dest: " + ip + ":" + port+ " from port: "+clientSocket.getLocalPort()+"\n"+newMsg + " through " + forwardIP+":"+forwardPort);
            clientSocket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, IPAddress, forwardPort));
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
            switch(input.split(" ")[0]) {
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

                case "CHANGE" : {
                    String[] change = input.split(" ");
                    if(change.length != 4) break;
//                    System.out.println("IP: " + change[1]);
//                    System.out.println("Port: " + change[2]);
//                    System.out.println("New weight: " + change[3]);
                    System.out.println("New weight to neighbor " + change[1] + ":" + change[2]);
//                    dv.put(change[1] + ":" + change[2], Integer.parseInt(change[3]));
                    original.put(change[1] + ":" + change[2], Integer.parseInt(change[3]));
                    updateDV();
                    try {
                        advertise();
                    } catch (Exception e) {e.printStackTrace();}
                    break;
                }

                case "MSG" : {
                    String msg = input.substring(19);
                    Scanner c = new Scanner(input);
                    c.next();
                    String sendIP = c.next();
                    int sendPort = Integer.parseInt(c.next().trim());
                    send(msg, sendIP, sendPort);
                    break;
                }

                default: {
                    System.out.println("command incorrect");
                    break;
                }
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

    public void setPoisonReverse(boolean val) {
        this.poisonReverse = val;
    }

    public void p(String s){
        System.out.println(s);
    }

    public static void main(String[] args) {
        if(args.length == 2){
            Router r1 = new Router(args[1]);
            r1.setPoisonReverse(true);
            r1.run();
        }
        else if(args.length == 1){
            Router r1 = new Router(args[0]);
            r1.run();
        }
        else System.out.println("wrong number of args");
    }

}
