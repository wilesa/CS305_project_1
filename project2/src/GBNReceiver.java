/**
 * Created by wilesa on 4/9/17.
 */
public class GBNReceiver {

    private NetworkLayer nl;
    private ReceiverApplication ra;
    private int seq = 0;
    private Packet lastSent;
    private int debug;

    public GBNReceiver(NetworkLayer nl, ReceiverApplication ra) {
        this.nl = nl;
        this.ra = ra;
        debug = 0;
    }

    public void setDebug(int i){debug = i;}

    public void gbn_rx(Packet pkt) {
        if(debug > 0) System.out.println("[RX] received: {Seq: " + pkt.getSeqnum() + ", " + pkt.getMessage().getMessage() + "}");
        if (pkt.isCorrupt()) {
            isCorrupt();
        } else if (pkt.getSeqnum() != seq) {
            outOfOrder(pkt.clone());
        } else {

            ra.receiveMessage(pkt.getMessage());
            Message m = new Message("ACK");
            Packet ack = new Packet(m, pkt.getSeqnum() + 1, pkt.getSeqnum(), 0);
            this.lastSent = ack;
            if(debug > 0) System.out.println("[RX] sending ACK: " + ack.getAcknum());
            nl.sendPacket(ack.clone(), Event.SENDER);
            seq++;
        }
    }

    public void outOfOrder(Packet pkt) {
        if(lastSent == null) return;
        nl.sendPacket(lastSent.clone(), Event.SENDER);
    }

    public void isCorrupt() {
        if(debug > 0) System.out.println("[RX] Corrupt");

    }

}
