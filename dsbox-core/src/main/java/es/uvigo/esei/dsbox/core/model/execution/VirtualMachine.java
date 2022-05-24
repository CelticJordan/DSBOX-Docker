package es.uvigo.esei.dsbox.core.model.execution;

import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.Host;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"type", "host"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VirtualMachine {
    @XmlAttribute(name="vm-type")
    private VMType type;
    
    @XmlIDREF
    @XmlAttribute(name="host-ref")
    private Host host;    

    public VirtualMachine() {
    }

    public VirtualMachine(VMType type) {
        this.type = type;
    }

    public VirtualMachine(VMType type, Host host) {
        this.type = type;
        this.host = host;
    }
    
    public abstract SessionSpec getSessionSpec();

    public VMType getType() {
        return type;
    }

    public void setType(VMType type) {
        this.type = type;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }


    
    
}
