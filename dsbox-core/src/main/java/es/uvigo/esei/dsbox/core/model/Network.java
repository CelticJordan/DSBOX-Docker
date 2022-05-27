package es.uvigo.esei.dsbox.core.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"id", "name", "type", "description", "netAddress", "netMask", "broadcast", "vmProperties"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Network {

    @XmlAttribute
    @XmlID
    private String id;
    @XmlAttribute
    private String name;

    @XmlAttribute
    private String label;

    private String description;

    @XmlElement(name="network-address")
    private String netAddress;

    @XmlElement(name="network-mask")
    private String netMask;

    @XmlElement(name="broadcast-address")    
    private String broadcast;    
    
    @XmlAttribute(name = "network-type")
    private NetworkType type;

    @XmlElementWrapper(name = "vm-properties", required = false)
    @XmlElement(name = "vm-property")
    private List<VMProperty> vmProperties;

    public Network() {
    }

    public Network(String id, String name, String description, NetworkType type) {
        this.id = id;
        this.name = name;
        this.label = normalizeNetworkName(name);
        this.description = description;
        this.type = type;
        this.netAddress = "10.10.10.0";
        this.netMask = "255.255.255.0";
        this.broadcast = "10.10.10.255";
    }

    public Network(String id, String name, String description, String netAddress, String netMask, String broadcast, NetworkType type) {
        this.id = id;
        this.name = name;
        this.label = normalizeNetworkName(name);
        this.description = description;
        this.netAddress = netAddress;
        this.netMask = netMask;
        this.broadcast = broadcast;
        this.type = type;
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public List<VMProperty> getVmProperties() {
        return vmProperties;
    }

    public void setVmProperties(List<VMProperty> vmProperties) {
        this.vmProperties = vmProperties;
    }

    public void addVMProperty(VMProperty vmProperty) {
        if (this.vmProperties == null) {
            this.vmProperties = new ArrayList<VMProperty>();
        }
        this.vmProperties.add(vmProperty);
    }

    public void removeVMProperty(VMProperty vmProperty) {
        if (this.vmProperties != null) {
            this.vmProperties.remove(vmProperty);
        }
    }

    public String getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(String netAddress) {
        this.netAddress = netAddress;
    }
    
    public String getNetMask() {
        return netMask;
    }

    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    
    private String normalizeNetworkName(String name) {
        return name.trim().replace(" ", "_");
    }

}
