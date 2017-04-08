import java.util.ArrayList;
import java.util.LinkedList;

public class GBN {

    private ArrayList<Packet> window;
    private LinkedList<Message> queue;

    private NetworkLayer nl;

    public GBN(NetworkLayer nl) {
        this.nl = nl;
    }

    public int gbn_rx(Packet pkt) {
        System.out.println("ACK " + pkt.getAcknum() + " recieved");
        if(!pkt.isCorrupt()){
            int ack = pkt.getAcknum();
            for(int i=0;i<window.size();i++){
                if(window.get(i).getSeqnum() <= ack) {
                    window.remove(i);
                    i--;
                }
                if(queue.size()!=0) {
                    sendMessage(queue.pop());
                }
            }
        }
        return 0;
    }

    public int gbn_tx(Message msg) {
        if(window.size() != n){
                        Packet p = new Packet(msg, seq++, 0, 0);
                        nl.sendPacket(p, 1); //Message arriving from sender to receiver
            // tl.createSendEvent();
            window.add(p);
                }
        else queue.addLast(msg);

        //nl.sendPacket(p, 0); //Message Arriving from receiver to sender
        return 0;
    }

}
