import java.util.Scanner;

/**
 * Created by Austin on 5/1/2017.
 */
public class RouterEntry {

    String ip;
    int port;
    int weight;

    public RouterEntry(String ip, String port, String weight) {
        this.ip = ip;
        this.port = Integer.parseInt(port.trim());
        this.weight = Integer.parseInt(weight.trim());

    }

    public RouterEntry(String ip, String port, int weight){
        this.ip = ip;
        this.port = Integer.parseInt(port.trim());
        this.weight = weight;
    }

    public RouterEntry(String key, String weight){
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

    public void setWeight(int weight) {this.weight = weight;}



}
