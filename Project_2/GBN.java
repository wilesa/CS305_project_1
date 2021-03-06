import java.util.ArrayList;
import java.util.LinkedList;

public class GBN {

    private ArrayList<Packet> window;
    private LinkedList<Message> queue;
    private int seq;
    private int windowSize;

    private NetworkLayer nl;

    public GBN(NetworkLayer nl, int windowSize) {
        this.nl = nl;
        this.seq = 0;
        this.windowSize = windowSize;
        this.window = new ArrayList<>();
        this.queue = new LinkedList<>();
        System.out.println("[GBN] window size: "+this.windowSize);
    }

    public void sender() {

    }

    public int gbn_tx(Message msg) {
        System.out.println("<Window> size: " + window.size() + "/" + this.windowSize);
        if(window.size() < windowSize){
            Packet p = new Packet(msg, seq++, 0, 0);
            System.out.println("[Sender] sending: "+msg.getMessage());
            nl.sendPacket(p, 1); //Message arriving from sender to receiver
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
            for(int i=0;i<window.size();i++){
                if(window.get(i).getSeqnum() <= ack) {
                    window.remove(i);
                    i--;
                }
                if(queue.size()!=0) {
                    gbn_tx(queue.pop());
                }
            }
        }
        return 0;
    }



}