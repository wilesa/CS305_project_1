import java.util.Scanner;

/**
 * Created by Austin on 5/1/2017.
 */
public class RouterEntry {
    String key;
    String ip;
    int port;
    int weight;

    public RouterEntry(String ip, String port, String weight) {
        key = ip.trim() + ":" + port.trim();
        this.ip = ip;
        this.port = Integer.parseInt(port.trim());
        this.weight = Integer.parseInt(weight.trim());

    }

    public RouterEntry(String ip, String port, int weight){
        key = ip.trim() + ":" + port.trim();
        this.ip = ip;
        this.port = Integer.parseInt(port.trim());
        this.weight = weight;
    }

    public RouterEntry(String key, String weight){
        this.key = key;
        this.ip = key.split(":")[0];
        this.port = Integer.parseInt(key.split(":")[1].trim());
        this.weight = Integer.parseInt(weight.trim());
    }

    public RouterEntry(String received_msg){

    }

    public String toString() {
        return this.ip + ":" + port + " " + weight;
    }

    public String getIP() {return this.ip;}
    public int getPort() {return this.port;}
    public int getWeight() {return this.weight;}
    public String getKey() {return this.key;}

    public void setWeight(int weight) {this.weight = weight;}

    public Boolean isDifferent(RouterEntry r_compare) {
        if(!r_compare.getIP().trim().equals(ip.trim()) || r_compare.getPort() != port || r_compare.getWeight() != weight) {
            System.out.println("RouterEntries different");
            System.out.println("ip: " + ip +", " + r_compare.getIP());
            System.out.println("port: " + port +", " + r_compare.getPort());
            System.out.println("weight: " + weight +", " + r_compare.getWeight());
            return true;
        }
        return false;
    }



}
