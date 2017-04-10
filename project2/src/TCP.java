import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.LinkedList;

public class TCP {

    public ArrayList<String> messages;

    private ArrayList<Packet> window;
    private LinkedList<Message> queue;
    private int seq;
    private int windowSize;

    private NetworkLayer nl;
    private Timeline tl;
    private int type;

    private int numAcks;
    private int lastAck;
    private int debug;



    public TCP(NetworkLayer nl, int windowSize, int type) {
        this.nl = nl;
        this.seq = 0;
        this.windowSize = windowSize;
        this.window = new ArrayList<>();
        this.queue = new LinkedList<>();
        this.type = type;
        this.lastAck = -1;
        debug = 0;
        numAcks = 0;
    }

    public void setDebug(int i) {debug=i;}

    public void tcp_tx(Message msg) {
        if(this.type == Event.SENDER) {
            String s= "";
            if(window != null) for(Packet pkt : window) s = s + pkt.getMessage().getMessage() + ", ";
            //System.out.println("<Window> "+s);
            if(window.size() < windowSize){
                Packet p = new Packet(msg, seq++, 0, 0);
                if(debug > 0) System.out.println("[TX] sending: {Seq: " + p.getSeqnum() +", " + p.getMessage().getMessage() +"}");
                nl.sendPacket(p.clone(), Event.RECEIVER); //Message arriving from sender to receiver
                //System.out.println("STARTING TIMER");
                if(tl.isNull()) tl.startTimer(50);

                // tl.createSendEvent();
                window.add(p);
            }
            else queue.addLast(msg);
        } else if(this.type == Event.RECEIVER) {

        }
        if(debug > 0) System.out.println();


    }

    public void tcp_rx(Packet pkt) {
        if(this.type == Event.SENDER) {
            if(debug > 0) System.out.println("[TX] ACK " + pkt.getAcknum() + " recieved");
            if(!pkt.isCorrupt()){
                int ack = pkt.getAcknum();
                //System.out.println("STOPPING TIMER");
                if(!tl.isNull())tl.stopTimer();
                for(int i=0;i<window.size();i++){
                    if(window.get(i).getSeqnum() <= ack-1) {
                        window.remove(i);
                        i--;
                        if (queue.size() != 0) {
                            tcp_tx(queue.pop());
                        }
                    }
                }
                if(checkFastRetransmit(ack)) {
                    if(debug > 0) System.out.println("[TX] FAST RETRANSMIT");
                    if(debug > 0) System.out.println("[TX] sending: {Seq: " + window.get(0).getSeqnum() +", " + window.get(0).getMessage().getMessage() +"}");
                    nl.sendPacket(window.get(0).clone(), Event.RECEIVER);
                }
                if(window.size()!=0) {
                    //System.out.println("STARTING TIMER");
                    if(tl.isNull())tl.startTimer(50);
                }

            } else {
                if(debug > 0) System.out.println("[TX] ACK Corrupt\n");
            }
            if(debug > 0) System.out.println();
        } else if (this.type == Event.RECEIVER) {

        }

    }

    public Boolean checkFastRetransmit(int ack) {
        if(ack > lastAck) {
            lastAck = ack;
            numAcks = 0;
            return false;
        }
        if(ack == lastAck) {
            numAcks++;
            if(debug > 0) System.out.println("[TX] #Duplicate ACKs = "+numAcks);
        }
        if(numAcks == 3) {
            numAcks = 0;
            return true;
        }
        return false;
    }

    
    public void tcp_timerExpired(){
        if(debug > 0) System.out.println("-------------------TIMER EXPIRED TCPSender-----------------");
//        for(Packet pkt : window) {
//            System.out.println("[TX] sending: {Seq: " + pkt.getSeqnum() +", " + pkt.getMessage().getMessage() +"}");
//            tl.startTimer(40);
//
//            nl.sendPacket(pkt.clone(), Event.RECEIVER);
//            System.out.println();
//        }

        if(debug > 0) System.out.println("[TX] sending: {Seq: " + window.get(0).getSeqnum() +", " + window.get(0).getMessage().getMessage() +"}");
        if(tl.isNull()) tl.startTimer(50);

        nl.sendPacket(window.get(0).clone(), Event.RECEIVER);
        if(debug > 0) System.out.println();

    }

    public void tcp_set_timeline(Timeline tl) {
        this.tl = tl;
    }

    public void set_window_size(int n){this.windowSize = n;}






}