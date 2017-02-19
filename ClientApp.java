
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

    public static void main(String[] args) throws Exception {
        try {
            version = args[0];
        } catch (Exception e) {version = "1.1";}
        //create a new transport layer for client (hence false) (connect to server), and read in first line from keyboard
        transportLayer = new TransportLayer(false, version);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        try{
            File cache = new File("cache");
            if(!cache.exists())
                cache.mkdir();
            // if(!Files.exists("Cache")) Files.createDirectory("Cache");
        }
        catch(Exception e){
            System.out.println("Failed to initialize cache");
        }

        //while line is not empty
        while( line != null && !line.equals("") )
        {
            //convert lines into byte array, send to transoport layer and wait for response
            // System.out.printf("%ta, %<te %<tb %<tY %<tT", new Date());
            // File inCache = new File("cache/" + line);
            // if(inCache.exists()){

            // }

            // else{
                // inCache.createNewFile();
                // String req = "GET /" + line + " HTTP/" + version;

                // sendGet(line);
                // byte[] byteArray = transportLayer.receive();
                // String str = new String ( byteArray );
                // System.out.println( "Received: " + str );
                // System.out.println("******** \n");
                String tml = processRequest(line);
                System.out.println(tml);
                // String[] strs = str.split("<text>|</text>");
                // String text;
                // if(strs.length > 1) text = processResponse(strs[1]);
            // }

            //read next line
            line = reader.readLine();

        }
    }

    private static String processRequest(String req) {
        HTTP message = new HTTP(version);
        message.set_get(req);
        if(Files.exists(Paths.get("cache/" + req))) {
            message.set_if_modified(makeDate("cache/" + req));
        }

        byte[] byteArray = message.toString().getBytes();
        if(!transportLayer.send(byteArray)) {
            System.out.println("Could not send message");
            return "Could not send message";
        }

        byteArray = transportLayer.receive();
        HTTP response = new HTTP(byteArray);
        if(response.isNotFound()) return "FILE NOT FOUND";
        else if(response.isNotModified()) return processTML(getFile("cache/" + req));
        else if(!response.hasContent()) return response.get_status();
        else {
            writeFile("cache/" + req, response.get_content());
            return processTML(response.get_content());
        }
    }

    private static String processTML(String tml) {
        if(tml.contains("<text>")){
            String[] temp;
            temp = tml.split("<text>|</text>");
            tml = temp[1];
        }

        if(!tml.contains("<embed>")) return tml;

        String[] strs = tml.split("<embed>|</embed>");
        try{
            String str = processRequest(strs[1].trim());
            strs[1] = str;
            String randomname = "";
            for(String s : strs) {
                randomname = randomname + s;
            }
            return randomname;
        } catch(Exception e) {return null;}
    }

    private static String makeDate(String req){
        //TO DO: actually format the date
        try{
            long time = Files.getLastModifiedTime(Paths.get(req)).toMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return dateFormat.format(calendar.getTime());    
        }
        catch(Exception e) {return "-1";}
    }

    private static String getFile(String filename) {
        try{
            return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8); 
        } catch(Exception e) {
            System.out.println("Requested file does not exist");
            return null;
        }
    }

    private static Boolean writeFile(String filename, String text) {
        try{
            if(!Files.exists(Paths.get(filename)))
                Files.createFile(Paths.get(filename));
            byte[] byteArray = text.getBytes();
            Files.write(Paths.get(filename), byteArray);
            return true;
        } catch(Exception e) {
            System.out.println("Failed to write");
            return false;
        }
    }



    private static String processResponse(String res) {
        HTTP http = new HTTP(res.getBytes());
        if(http.isNotFound()) return "FILE NOT FOUND";
        if(!http.hasContent()) return res;
        else{
            String tml = http.get_content();
            System.out.println(tml);
            System.out.println("******** \n");
            if(tml.contains("<text>")){
                String[] temp;
                temp = res.split("<text>|</text>");
                tml = temp[1];
            }
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
        }
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
    }

    private static Boolean sendGet(String req){
        HTTP message = new HTTP(version);
        message.set_get(req);
        byte[] byteArray = message.toString().getBytes();
        if(!transportLayer.send(byteArray)) {
            System.out.println("Could not send message");
            return false;
        }
        return true;
    }

}
