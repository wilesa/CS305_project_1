
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
        try {
            int v = args[0];
            if(v!=1.1 || v!=1.0) v = 1.1;
            Client c = new Client(useCache, v);
            c.run();
        } catch (Exception e) {
            Client c = new Client(useCache, "1.1");
            c.run();
        }
    }
