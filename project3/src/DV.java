import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by 13wil on 5/1/2017.
 */
public class DV {

    String source;
    HashMap<String, RouterEntry> routerMap;
    ArrayList<String> reachables;

    public DV(String dv) {
//        System.out.println(dv);
        routerMap = new HashMap<>();
        Scanner sc = new Scanner(dv);
        if(!sc.hasNextLine()) return;
        String[] line = sc.nextLine().split(" ");
        source = line[0]+":"+line[1];
        routerMap.put(line[0] + ":" + line[1], new RouterEntry(line[0], line[1], 0));
        while (sc.hasNextLine()) {
            line = sc.nextLine().split(" ");
            if(line.length != 3) System.out.println("LINE LENGTH WRONG");
            //if(line.length != 3) throw new Exception("Line expecting length of 3: length of line is " + line.length);
            routerMap.put(line[0]+":"+line[1], new RouterEntry(line[0], line[1], line[2]));
        }
        reachables = new ArrayList<>(routerMap.keySet());
        //for(String s : reachables) p(s);

    }

    public void setSource(String source){this.source = source;}
    public String getSource(){return this.source;}


    public void p(String s) {
        System.out.println(s);
    }

    public RouterEntry get(String key) {
        return routerMap.containsKey(key) ? routerMap.get(key) : null;
    }

    public ArrayList<String> getReachables() {return reachables;}

    public String toString() {
        String s = "";
        for(int i = 0; i < reachables.size(); i++) {
            s = s + routerMap.get(reachables.get(i)).toString();
            if(i != reachables.size()-1) s = s + "\n";
        }
        return s;
    }



}
