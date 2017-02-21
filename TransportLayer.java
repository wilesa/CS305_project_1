
public class TransportLayer
{
    private byte[] syn = "SYN".getBytes();
    private byte[] ack = "ACK".getBytes();
    private boolean connected = false;
    private boolean server;
    private String version = "1.1";

    private NetworkLayer networkLayer;
    //server is true if the application is a server (should listen) or false if it is a client (should try and connect)
    public TransportLayer(boolean server, String version)
    {
        networkLayer = new NetworkLayer(server);
        this.version = version;
        this.server = server;
    }

    public TransportLayer(boolean server, String version, int prop, int trans)
    {
        networkLayer = new NetworkLayer(server, prop, trans);
        this.version = version;
        this.server = server;
    }

    public boolean send(byte[] payload)
    {
        if((!this.connected || this.version.equals("1.0")) && !this.server) {
            //System.out.println("(Transport Layer) Sending SYN");
            networkLayer.send(syn);
            try {
                byte[] pl = networkLayer.receive();
                String res = new String(pl);
                //System.out.println("(Transport Layer) Received: " + res);
                if(res.equals("ACK")) {
                    this.connected = true;
                }
                else {
                    System.out.println("Did not receive ACK, instead received: "+res);
                    return false;
                }
            }
        catch (Exception e) { System.out.println("Terminated"); return false;}
        }

        String str = new String(payload);
        //System.out.println("(Transport Layer) Sending: " + str);
        networkLayer.send( payload );
        if(this.version.equals("1.0") && this.server) this.connected = false;
        return true;
    }

    public byte[] receive()
    {
        if(this.server && !this.connected) {
            try {
                byte[] payload = networkLayer.receive();
                String str = new String(payload);
                //System.out.println("(Transport Layer) Received " + str);
                if(str.equals("SYN")){
                    //System.out.println("(Transport Layer) Sending ACK");
                    networkLayer.send(ack);
                    this.connected = true;
                }
            } catch (Exception e) {
                System.out.println("Terminated");
                return null;
            }
        }
        byte[] payload;
        String str;
        try {
            payload = networkLayer.receive();
            str = new String(payload);
            if(str.contains("HTTP/1.0")) this.connected = false;
            if(this.version.equals("1.0") && !this.server) this.connected = false;
            //System.out.println(str);
            return payload;
        } catch(Exception e) {
            this.connected = false;
            return null;
        }
    }
}
