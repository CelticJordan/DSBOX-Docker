package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

//@XmlType(propOrder = {"host", "network", "interfaceOrder", "enabled",
//    "description", "ipAddress", "netMask", "broadcast"})
@XmlType(propOrder = {"host", "network", "interfaceOrder", "enabled",
    "description", "ipAddress"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {

    @XmlAttribute(name = "node-ref")
    @XmlIDREF
    private Host host;

    @XmlAttribute(name = "network-ref")
    @XmlIDREF
    private Network network;

    @XmlAttribute(name = "interface-order")
    private int interfaceOrder;

    private String description;

    @XmlElement(name = "ip-address")
    private String ipAddress;

    //@XmlElement(name="network-mask")
    //private String netMask;
    //private String broadcast;
    @XmlAttribute
    private boolean enabled;

    public Link() {
        this.enabled = true;
    }

    public Link(Host host, Network network, int interfaceOrder) {
        this.host = host;
        this.network = network;
        this.interfaceOrder = interfaceOrder;
        this.enabled = true;
        this.description = "link to " + this.network.getName();
        //this.netMask = "255.255.255.0";
        this.ipAddress = "10.10.10.11";
        //this.broadcast = "192.168.1.255";        
    }

//    public Link(Host host, Network network, int interfaceOrder, String ipAddress, String netMask, String broadcast) {
//        this(host, network, interfaceOrder);
//        this.setIpAddress(ipAddress);
//        this.setNetMask(netMask);
//        this.setBroadcast(broadcast);
//    }
    public Link(Host host, Network network, int interfaceOrder, String ipAddress) {
        this(host, network, interfaceOrder);
        this.setIpAddress(ipAddress);
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public int getInterfaceOrder() {
        return interfaceOrder;
    }

    public void setInterfaceOrder(int interfaceOrder) {
        this.interfaceOrder = interfaceOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

//    public String getNetMask() {
//        return netMask;
//    }
//
//    public void setNetMask(String netMask) {
//        this.netMask = netMask;
//    }
//
//    public String getBroadcast() {
//        return broadcast;
//    }
//
//    public void setBroadcast(String broadcast) {
//        this.broadcast = broadcast;
//    }
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
