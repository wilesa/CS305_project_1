
import java.util.ArrayList;
import java.util.LinkedList;
/**
 * A class which represents the receiver transport layer
 */
public class SenderTransport
{
    private NetworkLayer nl;
    private Timeline tl;
    private int n;
    private boolean usingTCP;
    private int seq;
    private ArrayList<Packet> window;
    private LinkedList<Message> queue;

    private ArrayList<String> messages;

    GBNSender gbn;
    TCP tcp;


    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();
        gbn = new GBNSender(nl, n, Event.SENDER);
        tcp = new TCP(nl, n, Event.SENDER);
    }

    public void initialize()
    {
        seq = 0;
        window = new ArrayList<Packet>();
        queue = new LinkedList<Message>();
    }

    public void setMessages(ArrayList<String> messages){
        this.messages = messages;
        if(gbn != null) gbn.messages = messages;
    }

    public void sendMessage(Message msg)
    {
        if(usingTCP) tcp.tcp_tx(msg);
        else gbn.gbn_tx(msg);
    }

    public void receiveMessage(Packet pkt)
    {
        if(usingTCP) tcp.tcp_rx(pkt);
        else gbn.gbn_rx(pkt);
    }

    public void timerExpired()
    {
        if(!usingTCP) gbn.gbn_timerExpired();
        else tcp.tcp_timerExpired();
    }

    public void setTimeLine(Timeline tl)
    {
        this.tl=tl;
        this.gbn.gbn_set_timeline(this.tl);
        this.tcp.tcp_set_timeline(this.tl);
    }

    public void setWindowSize(int n)
    {
        this.n=n;
        this.gbn.set_window_size(n);
        this.tcp.set_window_size(n);
    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}
