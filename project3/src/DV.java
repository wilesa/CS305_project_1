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
    public HashMap<String, Integer> routerMap;

    public DV(String dv) {
        source = null;
        routerMap = new HashMap<>();
        Scanner sc = new Scanner(dv);
        if(!sc.hasNextLine()) return;
        if(dv.contains(":")){
            while (sc.hasNextLine()) {
                String line[];
                line = sc.nextLine().trim().split(" ");
                if (line.length != 2) System.out.println("LINE LENGTH WRONG (expecting 2): " + line.toString());
                if(line[1].trim().equals("0")) source = line[0].trim();
                routerMap.put(line[0].trim(), Integer.parseInt(line[1].trim()));
            }
            if(source == null) {
                System.out.println("Could not find source");
                p(toString());
            }
        } else {
            String line[];
            line = sc.nextLine().split(" ");
            source = line[0]+":"+line[1];
            routerMap.put(source, 0);
            while (sc.hasNextLine()) {
                line = sc.nextLine().split(" ");
                if (line.length != 3) System.out.println("LINE LENGTH WRONG (expecting 3): " + line.toString());
                else routerMap.put(line[0] + ":" + line[1], Integer.parseInt(line[2]));
            }
        }
        ip = source.split(":")[0].trim();
        port = Integer.parseInt(source.split(":")[1].trim());
        //for(String s : reachables) p(s);

    }

    public DV() {
        routerMap = new HashMap<>();
    }

    public void put(String key, int weight) {
        if(routerMap.containsKey(key)) routerMap.replace(key, weight);
        else routerMap.put(key, weight);
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

    public int get(String key) {
        return routerMap.containsKey(key) ? routerMap.get(key) : -1;
    }

    public ArrayList<String> keySet() {return new ArrayList<>(routerMap.keySet());}

    public String toString() {
        String s = "";
        HashMap<String, Integer> rMap = (HashMap<String,Integer>)routerMap.clone();
        ArrayList<String> reachables = new ArrayList<>(routerMap.keySet());
        for(int i = 0; i < reachables.size(); i++) {
            s = s + reachables.get(i) + " " + rMap.get(reachables.get(i)).toString();
            if(i != reachables.size()-1) s = s + "\n";
        }
        return s.trim();
    }

    public Boolean containsKey(String key) {
        return routerMap.containsKey(key);
    }

    public String getStringPR(String dest, HashMap<String, String> forward){
        HashMap<String, Integer> pr = (HashMap<String, Integer>)routerMap.clone();
        for(String s : forward.keySet()){
            if(!s.equals(dest) && forward.get(s).equals(dest)) pr.replace(s, Integer.MAX_VALUE);
        }
        String s = "";
        ArrayList<String> out = new ArrayList<>(pr.keySet());
        for(int i = 0; i < out.size(); i++) {
            s = s + out.get(i) + " " + pr.get(out.get(i)).toString();
            if(i != out.size()-1) s = s + "\n";
        }
        return s.trim();
    }

    public void update(DV dv_new) {

    }

    public Boolean isDifferent(DV dv_compare) {
        ArrayList<String> dv_compare_reachables = dv_compare.keySet();
        ArrayList<String> reachables = keySet();
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
            if(routerMap.get(key) != dv_compare.get(key)) {
                return true;
            }
        }
        return false;
    }

}
