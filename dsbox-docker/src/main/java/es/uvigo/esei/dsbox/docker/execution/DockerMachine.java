package es.uvigo.esei.dsbox.docker.execution;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.VMImage;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.execution.SSHSessionSpec;
import es.uvigo.esei.dsbox.core.model.execution.SessionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"status", "dockerInternalMachineName", "dockerUUID", "sshPort"})
@XmlAccessorType(XmlAccessType.FIELD)
public class DockerMachine extends VirtualMachine {

    @XmlAttribute(name = "status")
    private DockerStatus status;

    @XmlElement(name = "internal-machine-name")
    private String dockerInternalMachineName;

    @XmlElement(name = "docker-uuid")
    private String dockerUUID;

    @XmlElement(name = "ssh-port")
    private Integer sshPort;

    public DockerMachine() {
        super(VMType.DOCKER);
        this.status = DockerStatus.NON_STARTED;
    }

    public DockerMachine(String dockerInternalMachineName, String dockerUUID, Integer sshPort) {
        super(VMType.DOCKER);
        this.status = DockerStatus.RUNNING;
        this.dockerUUID = dockerUUID;
        this.dockerInternalMachineName = dockerInternalMachineName;
        this.sshPort = sshPort;
    }

    public DockerMachine(Host host) {
        super(VMType.DOCKER, host);
        this.status = DockerStatus.NON_STARTED;
        this.dockerInternalMachineName = host.getName();
    }

    public DockerMachine(Host host, String dockerUUID, Integer sshPort) {
        super(VMType.DOCKER, host);
        this.status = DockerStatus.RUNNING;
        this.dockerUUID = dockerUUID;
        this.dockerInternalMachineName = host.getName();
        this.sshPort = sshPort;
    }
    
    
    public DockerStatus getStatus() {
        return status;
    }

    public void setStatus(DockerStatus status) {
        this.status = status;
    }

    public String getDockerInternalMachineName() {
        return dockerInternalMachineName;
    }

    public void setDockerInternalMachineName(String dockerInternalMachineName) {
        this.dockerInternalMachineName = dockerInternalMachineName;
    }

    public String getDockerUUID() {
        return dockerUUID;
    }

    public void setDockerUUID(String dockerUUID) {
        this.dockerUUID = dockerUUID;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    @Override
    public SessionSpec getSessionSpec() {
        VMImage vmImage = this.getHost().getHostType().getVmImage();
        return new SSHSessionSpec("localhost", this.sshPort, vmImage.getDefaultLogin(), vmImage.getDefaultPassword());
    }
        
    
    @XmlEnum(String.class)
    public static enum DockerStatus {
        PAUSED, RUNNING, SAVED, NON_STARTED
    }
}
