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

    public void ok() {
        this.start_line = "HTTP/" + this.version + " 200 OK";
    }

    public void not_modified() {
        this.start_line = "HTTP/" + this.version + " 304 Not Modified";
    }

    public void not_found() {
        this.start_line = "HTTP/" + this.version + " 404 Not Found";
    }

    public void if_modified(String date) {
        this.headers.add("If-Modified-Since: " + date);
    }

}