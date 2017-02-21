
public class NetworkLayer
{

    private LinkLayer linkLayer;
    private int propagation_delay;
    private int transmission_delay;
    public NetworkLayer(boolean server)
    {
        linkLayer = new LinkLayer(server);
        propagation_delay = 0;
        transmission_delay = 0;

    }

    public NetworkLayer(boolean server, int prop, int trans)
    {
        linkLayer = new LinkLayer(server);
        propagation_delay = prop;
        transmission_delay = trans;
    }

    public void send(byte[] payload){
        int t_delay = payload.length*transmission_delay;
        //System.out.println("(NetworkLayer) Transmission delay: "+ transmission_delay);
        try {
            Thread.sleep(t_delay + propagation_delay);
        }
        catch(Exception e) {
            System.out.println("ERROR IN NETWORKLAYER SLEEPING THREAD THING IN SEND METHOD");
        }
        linkLayer.send( payload );
    }

    public byte[] receive()
    {
        byte[] payload = linkLayer.receive();
        int t_delay = payload.length*transmission_delay;
        //System.out.println("(NetworkLayer) Transmission delay: "+ transmission_delay);
        try {
            Thread.sleep(t_delay + propagation_delay);
        }
        catch(Exception e) {
            System.out.println("ERROR IN NETWORKLAYER SLEEPING THREAD THING IN RECEIVE METHOD");
        }
        return payload;
    }

}
