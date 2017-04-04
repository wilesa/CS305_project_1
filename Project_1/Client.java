
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
public class Client
{
    private String version;
    private TransportLayer transportLayer;


    //Change this boolean to use or not use the cache
    private Boolean useCache = false; 

    public Client(Boolean useCache, String version){
        this.useCache = useCache;
        this.version = version;
        this.transportLayer = new TransportLayer(false, version);
    }

    //Main method for the class
    //Waits for user to input a requested file, then sends it to the processRequest method
    //Once file is sent to server, waits for return
    //Prints compleate recieved page, or error message
    //Prints the time it took to get response

    public void run() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();

        try{
            if(useCache){
                File cache = new File("cache");
                if(!cache.exists())
                    cache.mkdir();
            }
        }
        catch(Exception e){
            System.out.println("Failed to initialize cache");
        }

        //while line is not empty
        while( line != null && !line.equals("") )
        {
            //Save the time when the request is received.
            Calendar calendar = Calendar.getInstance();
            Long before = calendar.getTimeInMillis();

            //Send request to be processed
            String tml = processRequest(line);

            //Compare time with time request arrived, and print result
            Calendar after = Calendar.getInstance();
            Long diff = after.getTimeInMillis() - before;
            System.out.println("Time in ms: " + diff);
            System.out.println(tml);

            line = reader.readLine();

        }
    }

    //This method generates a GET http call with the user's unput.
    //If caching is on, this method will send a time modified with GET request
    //Also will write received tml to a file in the cache
    private String processRequest(String req) {
        HTTP message = new HTTP(version);
        message.set_get(req);
        if(useCache && Files.exists(Paths.get("cache/" + req))) {
            message.set_if_modified(makeDate("cache/" + req));
        }

        byte[] byteArray = message.toString().getBytes();
        if(!transportLayer.send(byteArray)) {
            System.out.println("Could not send message");
            return "Could not send message";
        }

        byteArray = transportLayer.receive();
        HTTP response = new HTTP(byteArray);
        //If 404
        if(response.isNotFound()) return "FILE NOT FOUND";
        //If 304
        else if(response.isNotModified()) return processTML(getFile("cache/" + req));
        //If something else, but doesn't contain a body
        else if(!response.hasContent()) return response.get_status();
        //if contains a body, write to cache if cache is enabled
        else {
            if(useCache) writeFile("cache/" + req, response.get_content());
            // System.out.println("response: " +response.get_content());
            return processTML(response.get_content());
        }
    }


    //This method handles a tml data packet.
    //It will remove all <text> and <embed> tags
    //Also will send a request for embeded files if they exist
    private String processTML(String tml) {
        //remove <text> wrappers
        if(tml.contains("<text>")){
            String[] temp;
            temp = tml.split("<text>|</text>");
            tml = temp[1];
        }

        //If no embedded files, done
        if(!tml.contains("<embed>")) return tml;

        //remove all <embed> wrappers
        String[] strs = tml.split("<embed>|</embed>");
        try{
            //for each split, check if it is an embedded file
            //If not, continue
            for(int i = 0; i < strs.length; i++){
                //too short to contain src= and a path, can't be embedded file
                if(strs[i].length() < 4) continue;
                //Doesn't start with src=, not embedded file
                if(!strs[i].substring(0, 4).equals("src=")) continue;
                String str = processRequest(strs[i].substring(4).trim());
                strs[i] = str;
            }
            //rebuild split string
            String randomname = "";
            for(String s : strs) {
                randomname = randomname + s;
            }
            //return rebuilt string
            return randomname;
        } 
        //if Exception, print error message and return null
        catch(Exception e) {e.printStackTrace();return null;}
    }

    //Generates a string with the date and time in http format
    //Takes a string file name, and gets is last modified date
    //Converts date to string
    //Returns -1 if file doesnt exist, or exception thrown
    private String makeDate(String req){
        try{
            //Get file last modified time in millis
            long time = Files.getLastModifiedTime(Paths.get(req)).toMillis();
            Calendar calendar = Calendar.getInstance();
            //turn file last modified time to calender
            calendar.setTimeInMillis(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            //convert calender to formatted string
            return dateFormat.format(calendar.getTime());    
        }
        catch(Exception e) {return "-1";}
    }

    //Gets the text body from a file given the file name
    //Returns null if file does not exist
    private String getFile(String filename) {
        try{
            return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8); 
        } catch(Exception e) {
            System.out.println("Requested file does not exist");
            return null;
        }
    }

    //Writes given text to a file with the given name
    //If a file with given name does not exist, creates one
    //If file does exist, will overwrite any text in file
    //Returns true if successful, false if exception thrown
    private Boolean writeFile(String filename, String text) {
        try{
            //If files doesn't exist, create one
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

}
