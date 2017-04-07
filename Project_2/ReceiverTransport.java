
/**
 * A class which represents the receiver transport layer
 */
public class ReceiverTransport
{
    private ReceiverApplication ra;
    private NetworkLayer nl;
    private boolean usingTCP;

    public ReceiverTransport(NetworkLayer nl){
        ra = new ReceiverApplication();
        this.nl=nl;
        initialize();
    }

    public void initialize()
    {
    }

    public void receiveMessage(Packet pkt)
    {
        
        // System.out.println("Receiver received: " + pkt.getMessage().getMessage());
        ra.receiveMessage(pkt.getMessage());
        Message m = new Message("");
        Packet ack = new Packet(m, pkt.getSeqnum()+1, pkt.getSeqnum(), 0);
        nl.sendPacket(ack, 0);
    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}
