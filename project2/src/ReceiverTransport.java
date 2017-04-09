import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A class which represents the receiver transport layer
 */
public class ReceiverTransport
{
    private ReceiverApplication ra;
    private NetworkLayer nl;
    private boolean usingTCP;

    private int expected_seq;

    private Packet lastSent;

    private PriorityQueue<Packet> buffer;
    private ArrayList<String> a;

    private GBNReceiver gbn;
    private TCPReceiver tcp;

    public ReceiverTransport(NetworkLayer nl){
        ra = new ReceiverApplication();
        this.nl=nl;
        expected_seq = 0;
        a = new ArrayList<>();
        buffer = new PriorityQueue<>();
        gbn = new GBNReceiver(nl, ra);
        tcp = new TCPReceiver(nl, ra);
        initialize();
    }

    public void initialize()
    {
    }

    public void receiveMessage(Packet pkt)
    {
        if(!usingTCP) {
            gbn.gbn_rx(pkt);

        }
        else {
            tcp.tcp_rx(pkt);
        }
    }

    

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}
