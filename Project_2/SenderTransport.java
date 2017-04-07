
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
	private int seq;
	private ArrayList<Packet> window;
    private Message queue;
	
	
    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();

    }

    public void initialize()
    {
		seq = 0;
		window = new ArrayList<Packet>();
    }

    public void sendMessage(Message msg)
    {
        if(window.size() != n){
			Packet p = new Packet(msg, seq++, 0, 0);
			nl.sendPacket(p, 1); //Message arriving from sender to receiver
            // tl.createSendEvent();
            window.add(p);
		}
        else queue = msg;
        
        //nl.sendPacket(p, 0); //Message Arriving from receiver to sender
    }

    public void receiveMessage(Packet pkt)
    {
        System.out.println("ACK " + pkt.getAcknum() + " recieved");
        if(!pkt.isCorrupt()){
            int ack = pkt.getAcknum();
            for(int i=0;i<window.size();i++){
                if(window.get(i).getSeqnum() <= ack) {
                    window.remove(i);
                    i--;
                }
                if(queue != null) {
                    sendMessage(queue);
                    queue = null;
                }
            }
        }
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
