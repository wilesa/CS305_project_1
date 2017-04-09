import java.util.ArrayList;

/**
 * A class which represents the receiver transport layer
 */
public class ReceiverTransport
{
    private ReceiverApplication ra;
    private NetworkLayer nl;
    private boolean usingTCP;

    private int seq;

    private Packet lastSent;

    private ArrayList<String> a;

    public ReceiverTransport(NetworkLayer nl){
        ra = new ReceiverApplication();
        this.nl=nl;
        seq = 0;
        a = new ArrayList<>();
        initialize();
    }

    public void initialize()
    {
    }

    public void receiveMessage(Packet pkt)
    {
        System.out.println("[Receiver] received: {Seq: " + pkt.getSeqnum() + ", " + pkt.getMessage().getMessage() + "}");
        if(pkt.isCorrupt()) {
            isCorrupt();
        }
        else if (pkt.getSeqnum() != seq){
            outOfOrder();
        }
        else {

            ra.receiveMessage(pkt.getMessage());
            Message m = new Message("ACK");
            Packet ack = new Packet(m, pkt.getSeqnum() + 1, pkt.getSeqnum(), 0);
            this.lastSent = ack;
            System.out.println("[Receiver] sending: " + ack.getAcknum());
            nl.sendPacket(ack.clone(), Event.SENDER);
            seq++;
            a.add(pkt.getMessage().getMessage());
        }
    }

    public void outOfOrder() {
        if(usingTCP) return;
        if(lastSent == null) return;
        nl.sendPacket(lastSent.clone(), Event.SENDER);
    }

    public void isCorrupt() {
        System.out.println("[RX] Corrupt");

    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}
