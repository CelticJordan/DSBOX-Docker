package es.uvigo.esei.dsbox.core.model.graphical;

import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class GraphicalSpec {
    @XmlElementWrapper(name = "host-positions")
    @XmlElement(name = "host-position")
    private List<HostPosition> hostPositions;

    @XmlElementWrapper(name = "network-positions")
    @XmlElement(name = "network-position")
    private List<NetworkPosition> networkPositions;

    // ¿Almacenar en enlaces (con que info.)?

    public List<HostPosition> getHostPositions() {
        return hostPositions;
    }

    public void setHostPositions(List<HostPosition> hostPositions) {
        this.hostPositions = hostPositions;
    }



    public List<NetworkPosition> getNetworkPositions() {
        return networkPositions;
    }

    public void setNetworkPositions(List<NetworkPosition> networkPositions) {
        this.networkPositions = networkPositions;
    }
 
    public void addHostPosition(HostPosition hostPosition) {
        if (this.hostPositions == null) {
            this.hostPositions = new ArrayList<HostPosition>();
        }
        this.hostPositions.add(hostPosition);
    }

    public void removeHostPosition(HostPosition nodePosition) {
        if (this.hostPositions != null) {
            this.hostPositions.remove(nodePosition);
        }
    }    

    public void addNetworkPosition(NetworkPosition networkPosition) {
        if (this.networkPositions == null) {
            this.networkPositions = new ArrayList<NetworkPosition>();
        }
        this.networkPositions.add(networkPosition);
    }

    public void removeNetworkPosition(NetworkPosition networkPosition) {
        if (this.networkPositions != null) {
            this.networkPositions.remove(networkPosition);
        }
    }    
    
}
