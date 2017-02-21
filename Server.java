import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Locale;

//This class represents the server application
public class Server {
    private String OK = "HTTP/1.1 200 OK";
    private String NOT_MODIFIED = "HTTP/1.1 304 Not Modified";
    private String NOT_FOUND = "HTTP/1.1 404 Not Found";

    private Boolean autoEmbed = true;
    private TransportLayer transportLayer;


    public Server(Boolean autoEmbed, int prop, int trans){
        this.transportLayer = new TransportLayer(true, "1.1", prop, trans);
        this.autoEmbed = autoEmbed;
        
    }

    public void run() throws Exception {
        while( true ) {
            try {
                //receive message from client, and send the "received" message back.
                byte[] byteArray = transportLayer.receive();
                //if client disconnected
                if(byteArray==null)
                    break;

                // String temp = new String ( byteArray );
                // System.out.println("recieved: " +  temp );

                HTTP req = new HTTP(byteArray);
                // System.out.println("made http");
                // if(req.toString() == null) System.out.println("failed to convert to string");
                // else System.out.println( "http to string: " + req.toString() );
                HTTP res = makeResponse(req);
                
                if(res == null) {
                    System.out.println("Response is null?");
                    continue;
                }
                String tempRes = res.toString();
                transportLayer.send( tempRes.getBytes() );
            } catch(Exception e) {
                continue;
            }
        }
    }


    //This method generates an http response based on the http request
    private HTTP makeResponse(HTTP req) {
        HTTP response = new HTTP("1.1");
        response.set_not_found();

        if(!req.isGet()){
            System.out.println("not valid http request");
            response.set_not_found();
            return response;
        } 

        String name = req.getFileName();
        // System.out.println("FILE NAME: " + name);
        if(name == null) {
            response.set_not_found();
            return response;
        }
        String file = getFile(name);
        // System.out.println("FILE CONTENTS: " + file);
        if(file == null) {
            response.set_not_found();
            return response;
        }

        if(this.autoEmbed && file.contains("<embed>")){
            HTTP embed = new HTTP("1.1");
            String[] strs = file.split("<embed>|</embed>");

            Boolean inCache = false;

            if(req.isIfModified() && !hasBeenModified(req.getLastModified(), req.getFileName())){
                embed.set_if_modified(req.getLastModified());
                inCache = true;
            }

            Boolean failed = false;

            try{
                for(int i = 0; i < strs.length; i++){
                    if(strs[i].length() < 4) continue;
                    if(!strs[i].substring(0, 4).equals("src=")) continue;
                    embed.set_get(strs[i].substring(4).trim());
                    HTTP next = makeResponse(embed);
                    //An embedded file was modified
                    // if(next == null) {
                    //     inCache = false;
                    //     break;
                    // }
                    //This means this embedded file has changed, but not its parent
                    //Unsure of state of other files embedded in same parent
                    //Need to abort attempt to auto embed from parent
                    if(embed.isIfModified() && next.isOk()) break; 

                    //Return 404 is status is 404
                    if(next.isNotFound()) return next;

                    if(next.isNotModified()) continue;

                    //rebuild message after split
                    //Only will hit this point if not checking if-modified-since
                    //Only does sonething if building an OK response with body
                    String[] temp = next.get_content().split("<text>|</text>");
                    strs[i] = temp[1];
                }

                if(!inCache){
                    String out = "";
                    for(String s : strs) {
                        out = out + s;
                    }
                    file = out;
                }
            } 
            catch(Exception e) {
                e.printStackTrace(); 
                response.set_not_found();
                return response;
            }
        }

        else if(req.isIfModified()){
            if(!hasBeenModified(req.getLastModified(), req.getFileName())) {
                response.set_not_modified();
                return response; 
            }
        }

        response.set_ok();
        response.set_content(file);
        return response;

        // switch(req[0].trim()) {
        //     case "GET":
        //         String file = getFile(req[1].trim());
        //         if(file == null) {
        //             return NOT_FOUND;
        //         } else {
        //             return OK + "\n\n" + file;
        //         }
        //     default:
        //         System.out.println("Not a HTTP method");
        //         return null;
        // }
    }

    


    private String getFile(String filename) {
        try{
            return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8); 
        } catch(Exception e) {
            System.out.println("Requested file does not exist");
            return null;
        }
    }

    private Boolean hasBeenModified(String time, String filename){
        try{
            long ourTime = Files.getLastModifiedTime(Paths.get(filename)).toMillis();

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d = dateFormat.parse(time);
            long thereTime = d.getTime();

            if(ourTime > thereTime) return true;
            else return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return true;
        }

    }

}

