
import java.lang.reflect.Array;
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

    GBN gbn;


    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();
        gbn = new GBN(nl, 4, Event.SENDER);
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
        gbn.gbn_tx(msg);
//        if(window.size() != n){
//            Packet p = new Packet(msg, seq++, 0, 0);
//            nl.sendPacket(p, Event.RECEIVER); //Message arriving from sender to receiver
//            // tl.createSendEvent();
//            window.add(p);
//        }
//        else queue.addLast(msg);
//
//        //nl.sendPacket(p, 0); //Message Arriving from receiver to sender
    }

    public void receiveMessage(Packet pkt)
    {
        gbn.gbn_rx(pkt);
//        System.out.println("ACK " + pkt.getAcknum() + " recieved");
//        if(!pkt.isCorrupt()){
//            int ack = pkt.getAcknum();
//            for(int i=0;i<window.size();i++){
//                if(window.get(i).getSeqnum() <= ack) {
//                    window.remove(i);
//                    i--;
//                }
//                if(queue.size()!=0) {
//                    sendMessage(queue.pop());
//                }
//            }
//        }
    }

    public void timerExpired()
    {
        gbn.gbn_timerExpired();
    }

    public void setTimeLine(Timeline tl)
    {
        this.tl=tl;
        this.gbn.gbn_set_timeline(this.tl);
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
