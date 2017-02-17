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
        this.content = content;
    }

    private void set_get_p(String get){this.start_line = get;}
    private void set_if_modified_p(String date) {this.headers.add(date);}

    public String get_status(){return this.start_line;}
    public ArrayList<String> get_headers(){return this.headers;}
    public String get_content(){return this.content;}

    public String toString(){
        if(this.start_line == null) return null;
        String http = this.start_line + "\n";
        for(String s : this.headers) http = http + s + "\n";
        if(this.content != null) http = http + "\n" + this.content;
        return http;
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

        for(int i = 0; i < lines.length; i++) {
            
        }
        return http;
    }

}