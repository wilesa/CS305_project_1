
import java.util.ArrayList;
/**
 * A class which represents the receiver transport layer
 */
public class SenderTransport
{
    private NetworkLayer nl;
    private Timeline tl;
    private int n;
    private boolean usingTCP;

    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();

    }

    public void initialize()
    {
    }

    public void sendMessage(Message msg)
    {
        Packet p = new Packet(msg, 0, 0, 0);
        nl.sendPacket(p, 1); //Message arriving from sender to receiver
        //nl.sendPacket(p, 0); //Message Arriving from receiver to sender
    }

    public void receiveMessage(Packet pkt)
    {
    }

    public void timerExpired()
    { 
    }

    public void setTimeLine(Timeline tl)
    {
        this.tl=tl;
    }

    public void setWindowSize(int n)
    {
        this.n=n;
    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}
