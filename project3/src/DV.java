import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by 13wil on 5/1/2017.
 */
public class DV {

    public String source;
    public String ip;
    public int port;
    public HashMap<String, RouterEntry> routerMap;

    public DV(String dv) {
        String line[];
        source = null;
        routerMap = new HashMap<>();
        Scanner sc = new Scanner(dv);
        if(!sc.hasNextLine()) return;
        if(dv.contains(":")){
            while (sc.hasNextLine()) {
                line = sc.nextLine().trim().split(" ");
                if (line.length != 2) System.out.println("LINE LENGTH WRONG (expecting 2)");
                if(line[1].equals("0")) source = line[0];
                routerMap.put(line[0], new RouterEntry(line[0], line[1]));
            }
            if(source == null) System.out.println("Could not find source");
        } else {
            line = sc.nextLine().split(" ");
            source = line[0]+":"+line[1];
            routerMap.put(line[0] + ":" + line[1], new RouterEntry(line[0], line[1], 0));
            while (sc.hasNextLine()) {
                line = sc.nextLine().split(" ");
                if (line.length != 3) System.out.println("LINE LENGTH WRONG (expecting 3)");
                else routerMap.put(line[0] + ":" + line[1], new RouterEntry(line[0], line[1], line[2]));
            }
        }
        ip = source.split(":")[0].trim();
        port = Integer.parseInt(source.split(":")[1].trim());
        //for(String s : reachables) p(s);

    }

    public DV() {
        routerMap = new HashMap<>();
    }

    public void put(String key, RouterEntry r) {
        if(routerMap.containsKey(key)) routerMap.replace(key, r);
        else routerMap.put(key, r);
    }

    public void setSource(String source){
        this.source = source;
        ip = source.split(":")[0].trim();
        port = Integer.parseInt(source.split(":")[1].trim());
    }

    public String getSource(){return this.source.trim();}


    public void p(String s) {
        System.out.println(s);
    }

    public RouterEntry get(String key) {
        return routerMap.containsKey(key) ? routerMap.get(key) : null;
    }

    public ArrayList<String> getReachables() {return new ArrayList<>(routerMap.keySet());}

    public String toString() {
        String s = "";
        ArrayList<String> reachables = getReachables();
        for(int i = 0; i < reachables.size(); i++) {
            s = s + routerMap.get(reachables.get(i)).toString();
            if(i != reachables.size()-1) s = s + "\n";
        }
        return s.trim();
    }

    public Boolean containsKey(String key) {
        return routerMap.containsKey(key);
    }

    public void update(DV dv_new) {

    }

    public Boolean isDifferent(DV dv_compare) {
        ArrayList<String> dv_compare_reachables = dv_compare.getReachables();
        ArrayList<String> reachables = getReachables();
        if(dv_compare_reachables.size() != reachables.size()) {
//            p("Sizes not equal");
            return true;
        }
        for(String key : dv_compare_reachables) {
            if(!routerMap.containsKey(key)) {
//                p("New key added");
                return true;
            }
        }
        for(String key : reachables) {
            if(!dv_compare.containsKey(key)) {
//                p("Key removed");
                return true;
            }
        }
        for(String key : reachables) {
            if(routerMap.get(key).isDifferent(dv_compare.get(key))) {
                return true;
            }
        }
        return false;
    }

}
