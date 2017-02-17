
public class NetworkLayer
{

    private LinkLayer linkLayer;
    private int propagation_delay = 1000;
    private int transmission_delay;
    public NetworkLayer(boolean server)
    {
        linkLayer = new LinkLayer(server);

    }
    public void send(byte[] payload){
        transmission_delay = payload.length*100;
        //System.out.println("(NetworkLayer) Transmission delay: "+ transmission_delay);
        try {
            //Thread.sleep(transmission_delay + propagation_delay);
        }
        catch(Exception e) {
            System.out.println("ERROR IN NETWORKLAYER SLEEPING THREAD THING IN SEND METHOD");
        }
        linkLayer.send( payload );
    }

    public byte[] receive()
    {
        byte[] payload = linkLayer.receive();
        return payload;
    }
}
