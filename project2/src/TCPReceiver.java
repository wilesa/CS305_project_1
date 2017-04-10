import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by wilesa on 4/9/17.
 */
public class TCPReceiver {

    private NetworkLayer nl;
    private ReceiverApplication ra;
    private int seq = 0;
    private Packet lastSent;
    private PriorityQueue<Packet> out_of_order;
    private ArrayList<Packet> in_order;
    private int debug;

    public TCPReceiver(NetworkLayer nl, ReceiverApplication ra) {
        this.nl = nl;
        this.ra = ra;
        debug = 0;
        out_of_order = new PriorityQueue<>();
        in_order = new ArrayList<>();
    }

    public void setDebug(int i) {this.debug = i;}

    public void tcp_rx(Packet pkt) {
        if(debug > 0) System.out.println("[RX] received: {Seq: " + pkt.getSeqnum() + ", " + pkt.getMessage().getMessage() + "}");
        if (pkt.isCorrupt()) {
            isCorrupt();
        } else if (pkt.getSeqnum() != seq) {
            outOfOrder(pkt.clone());
        } else {
            in_order.add(pkt.clone());
            ra.receiveMessage(pkt.getMessage());
            merge();
            Message m = new Message("ACK");
            Packet ack = new Packet(m, pkt.getSeqnum() + 1, in_order.get(in_order.size()-1).getSeqnum()+1, 0);
            this.lastSent = ack;
            if(debug > 0) System.out.println("[RX] sending ACK: " + ack.getAcknum());
            nl.sendPacket(ack.clone(), Event.SENDER);
            seq++;
        }

        if(debug > 0) {
            String s = "In order: ";
            for (Packet p : in_order) s = s + p.getSeqnum() + ", ";
            System.out.println(s);
            s = "Out of order: ";
            for (Packet p : out_of_order) s = s + p.getSeqnum() + ", ";
            System.out.println(s);
            System.out.println("Seq: " + seq);
            System.out.println();
        }
    }

    public void outOfOrder(Packet pkt) {
        Packet ack;
        if(!in_order.isEmpty()) ack = new Packet(new Message("ACK"), pkt.getSeqnum() + 1, in_order.get(in_order.size()-1).getSeqnum()+1, 0);
        else ack = new Packet(new Message("ACK"), pkt.getSeqnum() + 1, 0, 0);
        for(Packet p : in_order) {
            if(p.getSeqnum() == pkt.getSeqnum()) {
                if(debug > 0) System.out.println("Packet already buffered");
                if(debug > 0) System.out.println("[RX] sending ACK: " + ack.getAcknum());
                nl.sendPacket(ack, Event.SENDER);
                return;
            }
        }
        for(Packet p : out_of_order) {
            if(p.getSeqnum() == pkt.getSeqnum()) {
                if(debug > 0) System.out.println("Packet already buffered");
                if(debug > 0) System.out.println("[RX] sending ACK: " + ack.getAcknum());
                nl.sendPacket(ack, Event.SENDER);
                return;
            }
        }

        out_of_order.add(pkt);
        merge();
        if(lastSent == null) lastSent = new Packet(new Message("ACK"), pkt.getSeqnum()+1, 0, 0);
        if(debug > 0) System.out.println("[RX] sending ACK: " + ack.getAcknum());
        nl.sendPacket(ack, Event.SENDER);
        //System.out.println();
    }

    public void isCorrupt() {
        if(debug > 0) System.out.println("[RX] Corrupt\n");
    }

    public void merge(){
        if(!out_of_order.isEmpty() && !in_order.isEmpty() && out_of_order.peek().getSeqnum() == in_order.get(in_order.size()-1).getSeqnum()+1) {
            Packet p = out_of_order.poll();
            in_order.add(p);
            seq++;
            ra.receiveMessage(p.getMessage());
            merge();
        }
    }

}
