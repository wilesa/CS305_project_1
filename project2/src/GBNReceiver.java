/**
 * Created by wilesa on 4/9/17.
 */
public class GBNReceiver {

    private NetworkLayer nl;
    private ReceiverApplication ra;
    private int seq = 0;
    private Packet lastSent;

    public GBNReceiver(NetworkLayer nl, ReceiverApplication ra) {
        this.nl = nl;
        this.ra = ra;
    }

    public void gbn_rx(Packet pkt) {
        System.out.println("[RX] received: {Seq: " + pkt.getSeqnum() + ", " + pkt.getMessage().getMessage() + "}");
        if (pkt.isCorrupt()) {
            isCorrupt();
        } else if (pkt.getSeqnum() != seq) {
            outOfOrder(pkt.clone());
        } else {

            ra.receiveMessage(pkt.getMessage());
            Message m = new Message("ACK");
            Packet ack = new Packet(m, pkt.getSeqnum() + 1, pkt.getSeqnum(), 0);
            this.lastSent = ack;
            System.out.println("[RX] sending: " + ack.getAcknum());
            nl.sendPacket(ack.clone(), Event.SENDER);
            seq++;
        }
    }

    public void outOfOrder(Packet pkt) {
        if(lastSent == null) return;
        nl.sendPacket(lastSent.clone(), Event.SENDER);
    }

    public void isCorrupt() {
        System.out.println("[RX] Corrupt");

    }

}
