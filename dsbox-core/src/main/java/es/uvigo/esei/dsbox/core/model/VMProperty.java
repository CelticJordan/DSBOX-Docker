package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"name", "value"})
@XmlAccessorType(XmlAccessType.FIELD)
public class VMProperty {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String value;

    public VMProperty() {
    }

    public VMProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
