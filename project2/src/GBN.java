import java.util.ArrayList;
import java.util.LinkedList;

public class GBN {

    public ArrayList<String> messages;

    private ArrayList<Packet> window;
    private LinkedList<Message> queue;
    private int seq;
    private int windowSize;

    private NetworkLayer nl;
    private Timeline tl;

    public GBN(NetworkLayer nl, int windowSize) {
        this.nl = nl;
        this.seq = 0;
        this.windowSize = windowSize;
        this.window = new ArrayList<>();
        this.queue = new LinkedList<>();
        this.tl = tl;
        System.out.println("[GBN] window size: "+this.windowSize);
    }

    public void sender() {

    }

    public int gbn_tx(Message msg) {
        String s= "";
        if(window != null) for(Packet pkt : window) s = s + pkt.getMessage().getMessage() + ", ";
        //System.out.println("<Window> "+s);
        if(window.size() < windowSize){
            Packet p = new Packet(msg, seq++, 0, 0);
            System.out.println("[Sender] sending: {Seq: " + p.getSeqnum() +", " + p.getMessage().getMessage() +"}");
            nl.sendPacket(p, Event.RECEIVER); //Message arriving from sender to receiver

            tl.startTimer(10);

            // tl.createSendEvent();
            window.add(p);
        }
        else queue.addLast(msg);

        //nl.sendPacket(p, 0); //Message Arriving from receiver to sender
        return 0;
    }

    public int gbn_rx(Packet pkt) {
        System.out.println("[Sender] ACK " + pkt.getAcknum() + " recieved");
        if(!pkt.isCorrupt()){
            int ack = pkt.getAcknum();
            tl.stopTimer();
            for(int i=0;i<window.size();i++){
                if(window.get(i).getSeqnum() <= ack) {
                    window.remove(i);
                    i--;
                    if (queue.size() != 0) {
                        gbn_tx(queue.pop());
                    }
                }
            }
            if(window.size()!=0) tl.startTimer(10);

        }
        return 0;
    }


    public void gbn_timerExpired(){

    }

    public void gbn_set_timeline(Timeline tl) {
        this.tl = tl;
    }


}