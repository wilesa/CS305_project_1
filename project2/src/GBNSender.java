import java.util.ArrayList;
import java.util.LinkedList;

public class GBNSender {

    public ArrayList<String> messages;

    private ArrayList<Packet> window;
    private LinkedList<Message> queue;
    private int seq;
    private int windowSize;

    private NetworkLayer nl;
    private Timeline tl;
    private int type;
    private int debug;

    public GBNSender(NetworkLayer nl, int windowSize, int type) {
        this.nl = nl;
        this.seq = 0;
        this.windowSize = windowSize;
        this.window = new ArrayList<>();
        this.queue = new LinkedList<>();
        this.type = type;
        debug = 0;
    }

    public void setDebug(int i) {debug = i;}


    public int gbn_tx(Message msg) {
        if(this.type == Event.SENDER) {
            String s= "";
            if(window != null) for(Packet pkt : window) s = s + pkt.getMessage().getMessage() + ", ";
            //System.out.println("<Window> "+s);
            if(window.size() < windowSize){
                Packet p = new Packet(msg, seq++, 0, 0);
                if(debug > 0) System.out.println("[TX] sending: {Seq: " + p.getSeqnum() +", " + p.getMessage().getMessage() +"}");
                nl.sendPacket(p.clone(), Event.RECEIVER); //Message arriving from sender to receiver

                //System.out.println("STARTING TIMER");
                if(tl.isNull())tl.startTimer(50);

                // tl.createSendEvent();
                window.add(p);
            }
            else queue.addLast(msg);
        } else if(this.type == Event.RECEIVER) {

        }


        //nl.sendPacket(p, 0); //Message Arriving from receiver to sender
        return 0;
    }

    public int gbn_rx(Packet pkt) {
        if(this.type == Event.SENDER) {
            if(debug > 0) System.out.println("[TX] ACK " + pkt.getAcknum() + " recieved");
            if(!pkt.isCorrupt()){
                int ack = pkt.getAcknum();
                //System.out.println("STOPPING TIMER");
                if(!tl.isNull())tl.stopTimer();
                for(int i=0;i<window.size();i++){
                    if(window.get(i).getSeqnum() <= ack) {
                        window.remove(i);
                        i--;
                        if (queue.size() != 0) {
                            gbn_tx(queue.pop());
                        }
                    }
                }
                if(window.size()!=0) {
                    //System.out.println("STARTING TIMER");
                    if(tl.isNull())tl.startTimer(50);
                }

            } else {
                if(debug > 0) System.out.println("[TX] ACK Corrupt");
            }
        } else if (this.type == Event.RECEIVER) {

        }

        return 0;
    }


    public void gbn_timerExpired(){
        if(debug > 0) System.out.println("-------------------TIMER EXPIRED GBNSender-----------------");
        for(Packet pkt : window) {
            if(debug > 0) System.out.println("[TX] sending: {Seq: " + pkt.getSeqnum() +", " + pkt.getMessage().getMessage() +"}");
            if(tl.isNull())tl.startTimer(50);

            nl.sendPacket(pkt.clone(), Event.RECEIVER);
        }
    }

    public void gbn_set_timeline(Timeline tl) {
        this.tl = tl;
    }


    public void set_window_size(int n){this.windowSize = n;}

}