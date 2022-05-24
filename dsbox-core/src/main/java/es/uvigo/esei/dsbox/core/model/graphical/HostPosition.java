package es.uvigo.esei.dsbox.core.model.graphical;

import es.uvigo.esei.dsbox.core.model.Host;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
public class HostPosition extends GraphicalPosition {
    @XmlIDREF
    @XmlAttribute(name="host-ref")
    private Host host;

    public HostPosition() {
        super();
    }
    
    public HostPosition(Host host) {
        super();
        this.host = host;
    }
    
    public HostPosition(Host host, int x, int y) {
        super(x,y);
        this.host = host;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
    
    
}
