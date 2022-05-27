package es.uvigo.esei.dsbox.docker.execution;

import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.execution.VirtualNetwork;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"dockerNetworkId", "dockerNetworkName"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DockerNetwork extends VirtualNetwork{
      // TODO ¿que incluir aquí? info. necesaria para hacer referencia a una "red" de Docker
    @XmlElement(name = "docker-network-id")
    private String dockerNetworkId;
    
    @XmlElement(name = "docker-network-name")
    private String dockerNetworkName;

    public DockerNetwork() {
        super(VMType.DOCKER);
    }
  
    public DockerNetwork(String dockerNetworkId, String dockerNetworkName) {
        super(VMType.DOCKER);
        this.dockerNetworkId = dockerNetworkId;
        this.dockerNetworkName = dockerNetworkName;
    }

    public DockerNetwork(String dockerNetworkId, Network network) {
        super(VMType.DOCKER, network);
        this.dockerNetworkId = dockerNetworkId;
        this.dockerNetworkName = network.getName();
    }

    
    
    
    public String getDockerNetworkId() {
        return dockerNetworkId;
    }

    public void setDockerNetworkId(String dockerNetworkId) {
        this.dockerNetworkId = dockerNetworkId;
    }
   
    public String getDockerNetworkName() {
        return dockerNetworkName;
    }

    public void setDockerNetworkName(String dockerNetworkName) {
        this.dockerNetworkName = dockerNetworkName;
    }

  
      
}
