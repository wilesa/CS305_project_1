
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
        
        System.out.println("Receiver received: " + pkt.getMessage().getMessage());
    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}
