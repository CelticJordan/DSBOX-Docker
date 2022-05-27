package es.uvigo.esei.dsbox.core.model.graphical;

import es.uvigo.esei.dsbox.core.model.Host;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
public class NodePosition extends GraphicalPosition {
    @XmlIDREF
    @XmlAttribute(name="node-ref")
    private Host node;

    public NodePosition() {
        super();
    }
    
    public NodePosition(Host node) {
        super();
        this.node = node;
    }
    
    public NodePosition(Host node, int x, int y) {
        super(x,y);
        this.node = node;
    }

    public Host getNode() {
        return node;
    }

    public void setNode(Host node) {
        this.node = node;
    }
    
    
}
