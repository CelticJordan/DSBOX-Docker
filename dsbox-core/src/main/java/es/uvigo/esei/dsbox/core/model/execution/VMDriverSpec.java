package es.uvigo.esei.dsbox.core.model.execution;

import es.uvigo.esei.dsbox.core.model.VMType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {"type"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VMDriverSpec {
    @XmlAttribute(name="type")
    private VMType type;

    public VMDriverSpec() {
    }

    public VMDriverSpec(VMType type) {
        this.type = type;
    }

    public VMType getType() {
        return type;
    }

    public void setType(VMType type) {
        this.type = type;
    }
    
    
    
}
