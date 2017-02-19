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
public class ServerApp
{
    private static String OK = "HTTP/1.1 200 OK";
    private static String NOT_MODIFIED = "HTTP/1.1 304 Not Modified";
    private static String NOT_FOUND = "HTTP/1.1 404 Not Found";

    public static void main(String[] args) throws Exception {
        // HTTP http = new HTTP("1.1");
        // http.set_ok();
        // http.set_if_modified("now");
        // http.set_content(getFile("hello.tml"));
        // String httpstring = http.toString();
        // System.out.println(httpstring);
        // HTTP newHttp = new HTTP(httpstring.getBytes());
        // System.out.println(newHttp.toString());
        //create a new transport layer for server (hence true) (wait for client)
        TransportLayer transportLayer = new TransportLayer(true, "1.1");
        while( true ) {
            try {
                //receive message from client, and send the "received" message back.
                byte[] byteArray = transportLayer.receive();
                //if client disconnected
                if(byteArray==null)
                    break;

                String temp = new String ( byteArray );
                // System.out.println("recieved: " +  temp );

                HTTP req = new HTTP(byteArray);
                // System.out.println("made http");
                // if(req.toString() == null) System.out.println("failed to convert to string");
                // else System.out.println( "http to string: " + req.toString() );
                HTTP res = makeResponse(req);
                
                String tempRes = res.toString();
                if(tempRes == null) {
                    System.out.println("Response is null?");
                    continue;
                }
                transportLayer.send( tempRes.getBytes() );
            } catch(Exception e) {
                continue;
            }
        }
    }

    private static HTTP makeResponse(HTTP req) {
        HTTP response = new HTTP("1.1");
        response.set_not_found();

        if(!req.isGet()){
            System.out.println("not valid http request");
            response.set_not_found();
            return response;
        } 

        else if(req.isIfModified()){
            
            response.set_not_modified();
            return response;
        }

        else{
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
            else{
                // System.out.println("file found!");
                response.set_ok();
                response.set_content(file);
                return response;
            }
        }

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

    private static String getFile(String filename) {
        try{
            return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8); 
        } catch(Exception e) {
            System.out.println("Requested file does not exist");
            return null;
        }
    }

    private static Boolean hasBeenModified(String time, String filename){
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


// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.nio.file.Paths;
// import java.nio.file.Files;
// import java.nio.charset.StandardCharsets;

// //This class represents the server application
// public class ServerApp
// {
//     private static String OK = "HTTP/1.1 200 OK";
//     private static String NOT_MODIFIED = "HTTP/1.1 304 Not Modified";
//     private static String NOT_FOUND = "HTTP/1.1 404 Not Found";

//     public static void main(String[] args) throws Exception {
//         //create a new transport layer for server (hence true) (wait for client)
//         TransportLayer transportLayer = new TransportLayer(true, "1.1");
//         while( true ) {
//             try {
//                 //receive message from client, and send the "received" message back.
//                 byte[] byteArray = transportLayer.receive();
//                 //if client disconnected
//                 if(byteArray==null)
//                     break;
//                 String req = new String ( byteArray );
//                 System.out.println( req );
//                 String res = makeResponse(req.split("/|HTTP/|/nIf-Modified-Since:"));
//                 if(res == null) {
//                     System.out.println("Response is null?");
//                     continue;
//                 }
//                 transportLayer.send( res.getBytes() );
//             } catch(Exception e) {
//                 continue;
//             }
//         }
//     }

//     private static String makeResponse(String[] req) {
//         if(req.length < 3) {
//             System.out.println("Not a proper HTTP request");
//             return null;
//         }
//         switch(req[0].trim()) {
//             case "GET":
//                     String file = getFile(req[1].trim());
//                     if(file == null) {
//                         return NOT_FOUND;
//                     } else {
//                         if(req[2] != null){
//                             // Date date = DateFormat.parse(req[2]);
//                             // long orgDate = date.LONG;
//                             long orgDate = long.parseLong(req[2].trim);
//                             if(file.lastModified() < orgDate)
//                                 return NOT_MODIFIED;
//                         }
//                         return OK + "\n\n" + file;
//                     }
//             default:
//                 System.out.println("Not a HTTP method");
//                 return null;
//         }
//     }

//     private static String getFile(String filename) {
//         System.out.println(filename);
//         try{
//             return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8); 
//         } catch(Exception e) {
//             System.out.println("Requested file does not exist");
//             return null;
//         }
//     }
// }