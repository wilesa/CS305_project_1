
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;

//This class represents the client application
public class ClientApp
{
    private static String version;
    private static TransportLayer transportLayer;

    public static void main(String[] args) throws Exception {
        try {
            version = args[0];
        } catch (Exception e) {version = "1.1";}
        //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard
        transportLayer = new TransportLayer(false, version);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        //while line is not empty
        while( line != null && !line.equals("") )
        {
            //convert lines into byte array, send to transoport layer and wait for response
            // System.out.printf("%ta, %<te %<tb %<tY %<tT", new Date());
            String req = "GET /" + line + " HTTP/" + version;
            byte[] byteArray = req.getBytes();
            if(!transportLayer.send(byteArray)) {
                System.out.println("Could not send message");
                continue;
            }
            byteArray = transportLayer.receive();
            String str = new String ( byteArray );
            System.out.println( "Received: " + str );
            String tml = processResponse(str);
            System.out.println(tml);
            // String[] strs = str.split("<text>|</text>");
            // String text;
            // if(strs.length > 1) text = processResponse(strs[1]);

            //read next line
            line = reader.readLine();

        }
    }

    private static String processResponse(String res) {
        String[] stuff = res.split("<text>|</text>");
        if(stuff[0].contains("404 Not Found")) return "FILE NOT FOUND";
        if(stuff.length < 2) return res;
        String tml = stuff[1];
        if(!tml.contains("<embed>")) return tml;
        String[] strs = tml.split("<embed>|</embed>");
        String req = "GET /" + strs[1].trim() + " HTTP/" + version;
        transportLayer.send(req.getBytes());
        try{
            byte[] byteArray = transportLayer.receive();
            String received = new String(byteArray);
            String str = processResponse(received);
            strs[1] = str;
            String randomname = "";
            for(String s : strs) {
                randomname = randomname + s;
            }
            return randomname;
        } catch(Exception e) {return null;}
        //return "d";
    }

}
