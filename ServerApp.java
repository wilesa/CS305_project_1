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

    private static Boolean autoEmbed = false;

    public static void main(String[] args) throws Exception {
        try{
            int prop = Integer.parseInt(args[0]);
            int trans = Integer.parseInt(args[1]);
            Server server1 = new Server(autoEmbed, prop, trans);
            server1.run();
        }
        catch(Exception e) {
            Server server1 = new Server(autoEmbed, 0, 0);
            server1.run();
        }
        
    }
