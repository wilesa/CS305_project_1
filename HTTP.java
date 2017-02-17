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

    public void get(String url) {
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

    public String get_status(){return this.start_line;}
    public ArrayList<String> get_headers(){return this.headers;}
    public String get_content(){return this.content;}

    public String toString(){
        if(this.start_line == null) return null;
        String http = this.start_line + "\n";
        for(String s : this.headers) http = http + s + "\n";
        if(this.content != null) http = http + "\n" + this.content;
    }

}