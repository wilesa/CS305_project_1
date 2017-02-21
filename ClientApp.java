
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Locale;

//This class represents the client application
public class ClientApp
{
    private static String version;
    private static TransportLayer transportLayer;


    //Change this boolean to use or not use the cache
    private static Boolean useCache = false; 

    public static void main(String[] args) throws Exception {
        // try {
        //     version = args[0];
        // } catch (Exception e) {version = "1.1";}
        // //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard
        // transportLayer = new TransportLayer(false, version);
        // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // String line = reader.readLine();

        // try{
        //     if(useCache){
        //         File cache = new File("cache");
        //         if(!cache.exists())
        //             cache.mkdir();
        //     }
        // }
        // catch(Exception e){
        //     System.out.println("Failed to initialize cache");
        // }

        // //while line is not empty
        // while( line != null && !line.equals("") )
        // {
        //     //convert lines into byte array, send to transoport layer and wait for response
        //     // System.out.printf("%ta, %<te %<tb %<tY %<tT", new Date());
        //     // File inCache = new File("cache/" + line);
        //     // if(inCache.exists()){

        //     // }

        //     // else{
        //         // inCache.createNewFile();
        //         // String req = "GET /" + line + " HTTP/" + version;

        //         // sendGet(line);
        //         // byte[] byteArray = transportLayer.receive();
        //         // String str = new String ( byteArray );
        //         // System.out.println( "Received: " + str );
        //         // System.out.println("******** \n");
        //         Calendar calendar = Calendar.getInstance();
        //         Long before = calendar.getTimeInMillis();
        //         String tml = processRequest(line);
        //         Calendar after = Calendar.getInstance();
        //         Long diff = after.getTimeInMillis() - before;
        //         System.out.println("Time in ms: " + diff);
        //         System.out.println(tml);
        //         // String[] strs = str.split("<text>|</text>");
        //         // String text;
        //         // if(strs.length > 1) text = processResponse(strs[1]);
        //     // }

        //     //read next line
        //     line = reader.readLine();

        // }
        try {
            Client c = new Client(false, args[0]);
            c.run();
        } catch (Exception e) {
            Client c = new Client(false, "1.1");
            c.run();
        }
    }

    // //This method generates a GET http call with the user's unput.
    // //If caching is on, this method will send a time modified with GET request
    // //Also will write received tml to a file in the cache
    // private static String processRequest(String req) {
    //     HTTP message = new HTTP(version);
    //     message.set_get(req);
    //     if(useCache && Files.exists(Paths.get("cache/" + req))) {
    //         message.set_if_modified(makeDate("cache/" + req));
    //     }

    //     byte[] byteArray = message.toString().getBytes();
    //     if(!transportLayer.send(byteArray)) {
    //         System.out.println("Could not send message");
    //         return "Could not send message";
    //     }

    //     byteArray = transportLayer.receive();
    //     HTTP response = new HTTP(byteArray);
    //     //If 404
    //     if(response.isNotFound()) return "FILE NOT FOUND";
    //     //If 304
    //     else if(response.isNotModified()) return processTML(getFile("cache/" + req));
    //     //If something else, but doesn't contain a body
    //     else if(!response.hasContent()) return response.get_status();
    //     //if contains a body, write to cache if cache is enabled
    //     else {
    //         if(useCache) writeFile("cache/" + req, response.get_content());
    //         // System.out.println("response: " +response.get_content());
    //         return processTML(response.get_content());
    //     }
    // }


    // //This method handles a tml data packet.
    // //It will remove all <text> and <embed> tags
    // //Also will send a request for embeded files if they exist
    // private static String processTML(String tml) {
    //     if(tml.contains("<text>")){
    //         String[] temp;
    //         temp = tml.split("<text>|</text>");
    //         tml = temp[1];
    //     }

    //     if(!tml.contains("<embed>")) return tml;

    //     String[] strs = tml.split("<embed>|</embed>");
    //     try{
    //         for(int i = 0; i < strs.length; i++){
    //             if(strs[i].length() < 4) continue;
    //             if(!strs[i].substring(0, 4).equals("src=")) continue;
    //             String str = processRequest(strs[i].substring(4).trim());
    //             strs[i] = str;
    //         }
    //         String randomname = "";
    //         for(String s : strs) {
    //             randomname = randomname + s;
    //         }
    //         return randomname;
    //     } catch(Exception e) {e.printStackTrace();return null;}
    // }

    // private static String makeDate(String req){
    //     //TO DO: actually format the date
    //     try{
    //         long time = Files.getLastModifiedTime(Paths.get(req)).toMillis();
    //         Calendar calendar = Calendar.getInstance();
    //         calendar.setTimeInMillis(time);
    //         SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    //         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    //         return dateFormat.format(calendar.getTime());    
    //     }
    //     catch(Exception e) {return "-1";}
    // }

    // private static String getFile(String filename) {
    //     try{
    //         return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8); 
    //     } catch(Exception e) {
    //         System.out.println("Requested file does not exist");
    //         return null;
    //     }
    // }

    // private static Boolean writeFile(String filename, String text) {
    //     try{
    //         if(!Files.exists(Paths.get(filename)))
    //             Files.createFile(Paths.get(filename));
    //         byte[] byteArray = text.getBytes();
    //         Files.write(Paths.get(filename), byteArray);
    //         return true;
    //     } catch(Exception e) {
    //         System.out.println("Failed to write");
    //         return false;
    //     }
    // }



    // private static String processResponse(String res) {
    //     HTTP http = new HTTP(res.getBytes());
    //     if(http.isNotFound()) return "FILE NOT FOUND";
    //     if(!http.hasContent()) return res;
    //     else{
    //         String tml = http.get_content();
    //         System.out.println(tml);
    //         System.out.println("******** \n");
    //         if(tml.contains("<text>")){
    //             String[] temp;
    //             temp = res.split("<text>|</text>");
    //             tml = temp[1];
    //         }
    //         if(!tml.contains("<embed>")) return tml;
    //         String[] strs = tml.split("<embed>|</embed>");
    //         String req = "GET /" + strs[1].trim() + " HTTP/" + version;
    //         transportLayer.send(req.getBytes());
    //         try{
    //             byte[] byteArray = transportLayer.receive();
    //             String received = new String(byteArray);
    //             String str = processResponse(received);
    //             strs[1] = str;
    //             String randomname = "";
    //             for(String s : strs) {
    //                 randomname = randomname + s;
    //             }
    //             return randomname;
    //         } catch(Exception e) {return null;}
    //     }
        // String[] stuff = res.split("<text>|</text>");
        // if(stuff[0].contains("404 Not Found")) return "FILE NOT FOUND";
        // if(stuff.length < 2) return res;
        // String tml = stuff[1];
        // if(!tml.contains("<embed>")) return tml;
        // String[] strs = tml.split("<embed>|</embed>");
        // String req = "GET /" + strs[1].trim() + " HTTP/" + version;
        // transportLayer.send(req.getBytes());
        // try{
        //     byte[] byteArray = transportLayer.receive();
        //     String received = new String(byteArray);
        //     String str = processResponse(received);
        //     strs[1] = str;
        //     String randomname = "";
        //     for(String s : strs) {
        //         randomname = randomname + s;
        //     }
        //     return randomname;
        // } catch(Exception e) {return null;}
        //return "d";
    // }

    // private static Boolean sendGet(String req){
    //     HTTP message = new HTTP(version);
    //     message.set_get(req);
    //     byte[] byteArray = message.toString().getBytes();
    //     if(!transportLayer.send(byteArray)) {
    //         System.out.println("Could not send message");
    //         return false;
    //     }
    //     return true;
    // }

}
