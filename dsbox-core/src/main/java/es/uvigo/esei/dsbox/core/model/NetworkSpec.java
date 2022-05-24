package es.uvigo.esei.dsbox.core.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"hostTypes", "networks", "hosts"})
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkSpec {

    @XmlElementWrapper(name = "host-types")
    @XmlElement(name = "host-type")
    private List<HostType> hostTypes;

    @XmlElementWrapper(name = "networks")
    @XmlElement(name = "network")
    private List<Network> networks;

    @XmlElementWrapper(name = "hosts")
    @XmlElement(name = "host")
    private List<Host> hosts;

    public NetworkSpec() {
//        this.nodeTypes = new ArrayList<NodeType>();
//        this.networks = new ArrayList<Network>();
//        this.nodes = new ArrayList<Node>();        
    }

    public List<HostType> getHostTypes() {
        return hostTypes;
    }

    public void setHostTypes(List<HostType> hostTypes) {
        this.hostTypes = hostTypes;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }


    public List<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }


    public void addHostType(HostType hostType) {
        if (this.hostTypes == null) {
            this.hostTypes = new ArrayList<HostType>();
        }
        this.hostTypes.add(hostType);
    }

    public void addNetwork(Network network) {
        if (this.networks == null) {
            this.networks = new ArrayList<Network>();
        }
        this.networks.add(network);
    }

    public void addHost(Host node) {
        if (this.hosts == null) {
            this.hosts = new ArrayList<Host>();
        }
        this.hosts.add(node);
    }

    public void removeHostType(HostType hostType) {
        if (this.hostTypes != null) {
            this.hostTypes.remove(hostType);
        }
    }

    public void removeNetwork(Network network) {
        if (this.networks != null) {
            this.networks.remove(network);
        }
    }

    public void removeHost(Host host) {
        if (this.hosts != null) {
            this.hosts.remove(host);
        }
    }

}
