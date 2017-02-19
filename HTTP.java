import java.util.ArrayList;

public class HTTP {

    private String start_line;
    private ArrayList<String> headers;
    private String content;
    private String version;

    public HTTP(String version) {
        this.start_line = null;
        this.headers = new ArrayList<>();
        this.content = null;
        this.version = version;
    }

    public HTTP(byte[] httpArray) {
        this.headers = new ArrayList<>();
        String httpString = new String(httpArray);
        String lines[] = httpString.split("\r?\n");

        // for(int i=0; i<lines.length;i++)
        //     System.out.println("Line " + i  + " in lines: " + lines[i]);

        if(lines[0].contains("HTTP/1.0")) this.version = "1.0";
        else if(lines[0].contains("HTTP/1.1")) this.version = "1.1";
        else this.version = "1.1";

        if(lines[0].contains("GET")) this.set_get_p(lines[0]);
        else if(lines[0].contains("200")) this.set_ok();
        else if(lines[0].contains("304")) this.set_not_modified();
        else if(lines[0].contains("404")) this.set_not_found();
        else this.set_not_found();
        
        if(lines.length > 1) {
            if(!lines[1].trim().isEmpty() && lines[1].contains("If-Modified-Since")) {
                this.set_if_modified_p(lines[1]);
            }
            else {
                String content = "";
                for(int i = 1; i < lines.length; i++) {
                    content = content + "\n" + lines[i];
            }

            // if(lines.length >= 3) {
            //     String content = "";
            //     for(int i = 2; i < lines.length; i++) {
            //         content = content + lines[i] +"\n";
            //     }
                this.set_content(content);
            }
        }
    }

    public void set_get(String url) {
        this.start_line = "GET /" + url + " HTTP/" + this.version;
    }

    public void set_ok() {
        this.start_line = "HTTP/" + this.version + " 200 OK";
    }

    public void set_not_modified() {
        this.start_line = "HTTP/" + this.version + " 304 Not Modified";
    }

    public void set_not_found() {
        this.start_line = "HTTP/" + this.version + " 404 Not Found";
    }

    public void set_if_modified(String date) {
        this.headers.add("If-Modified-Since: " + date);
    }

    

    public void set_content(String content){
        this.content = content;                // String req = new String ( byteArray );
                // System.out.println( req );
                // String res = makeResponse(req.split("/|HTTP/"));
    }

    public String getFileName(){
        if(!isGet()) return null;
        String[] temp = get_status().split("/|HTTP/");
        return temp[1].trim();
    }

    public boolean isGet() {return get_status().contains("GET");} 
    public boolean isOk() {return get_status().contains("200");}
    public boolean isNotModified() {return get_status().contains("304");}
    public boolean isNotFound() {return get_status().contains("404");}
    public boolean isIfModified() {return !get_headers().isEmpty() && get_headers().get(0).contains("If-Modified-Since");}
    public boolean hasContent() {return this.content == null ? false : true;}
    

    public String get_status(){return this.start_line;}
    public ArrayList<String> get_headers(){return this.headers;}
    public String get_content(){return this.content;}


    public String toString(){
        try{
            if(this.start_line == null) return null;
            String http = this.start_line;
            if(!this.headers.isEmpty())
                for(int i=0;i<this.headers.size();i++) 
                    http = http + "\n" + this.headers.get(i);
            if(this.content != null) http = http + "\n" + this.content;
            return http;
        }
        catch(Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    

    public static HTTP fromString(String httpString) {
        HTTP http;
        String lines[] = httpString.split("\\r?\\n");

        if(lines[0].contains("HTTP/1.0")) http = new HTTP("1.0");
        else if(lines[0].contains("HTTP/1.1")) http = new HTTP("1.1");
        else http = new HTTP("1.1");

        if(lines[0].contains("GET")) http.set_get_p(lines[0]);
        else if(lines[0].contains("200")) http.set_ok();
        else if(lines[0].contains("304")) http.set_not_modified();
        else if(lines[0].contains("404")) http.set_not_found();
        
        if(lines.length == 1) return http;

        if(!lines[1].trim().isEmpty() && lines[1].contains("If-Modified-Since")) http.set_if_modified_p(lines[1]);

        if(lines.length < 3) return http;
        String content = "";
        for(int i = 2; i < lines.length; i++) {
            content = content + lines[i] +"\n";
        }
        http.set_content(content);
        return http;
    }


    private void set_get_p(String get){this.start_line = get;}
    private void set_if_modified_p(String date) {this.headers.add(date);}

}