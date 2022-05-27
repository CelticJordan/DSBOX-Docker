package es.uvigo.esei.dsbox.core.model.graphical;

import es.uvigo.esei.dsbox.core.model.Network;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkPosition extends GraphicalPosition {

    @XmlIDREF
    @XmlAttribute(name = "network-ref")
    private Network network;

    public NetworkPosition() {
        super();
    }

    
    public NetworkPosition(Network network) {
        super();
        this.network = network;
    }
    
    public NetworkPosition(Network network, int x, int y) {
        super(x,y);
        this.network = network;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    
    
}
