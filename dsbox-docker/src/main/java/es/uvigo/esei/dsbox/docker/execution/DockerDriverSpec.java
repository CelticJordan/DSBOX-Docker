package es.uvigo.esei.dsbox.docker.execution;

import es.uvigo.esei.dsbox.core.config.DSBOXConfig;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"dockerVersion", "dockerServerHost", "dockerServerPort", "dockerServerUser", "dockerServerPassword"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "docker-driver-spec")
public class DockerDriverSpec extends VMDriverSpec {

    public static String DOCKERDRIVER_CONFIG_FILE = "dockerDriver.xml";

    @XmlAttribute(name = "docker-version")
    private String dockerVersion;

    @XmlElement(name = "docker-server-host")
    private String dockerServerHost;

    @XmlElement(name = "docker-server-port")
    private Integer dockerServerPort;

    @XmlElement(name = "docker-server-user")
    private String dockerServerUser;

    @XmlElement(name = "docker-server-password")
    private String dockerServerPassword;

    // TODO: otra configuracion que necesite el DockerDriver

    public DockerDriverSpec() {
        super(VMType.DOCKER);
        this.dockerServerHost = "docker";
        this.dockerServerPort = 10022;
    }    
    
    
    public String getDockerVersion() {
        return dockerVersion;
    }

    public void setDockerVersion(String dockerVersion) {
        this.dockerVersion = dockerVersion;
    }

    public String getDockerServerHost() {
        return dockerServerHost;
    }

    public void setDockerServerHost(String dockerServerHost) {
        this.dockerServerHost = dockerServerHost;
    }

    public Integer getDockerServerPort() {
        return dockerServerPort;
    }

    public void setDockerServerPort(Integer dockerServerPort) {
        this.dockerServerPort = dockerServerPort;
    }

    public String getDockerServerUser() {
        return dockerServerUser;
    }

    public void setDockerServerUser(String dockerServerUser) {
        this.dockerServerUser = dockerServerUser;
    }

    public String getDockerServerPassword() {
        return dockerServerPassword;
    }

    public void setDockerServerPassword(String dockerServerPassword) {
        this.dockerServerPassword = dockerServerPassword;
    }

    
    

    public static final DockerDriverSpec createDefaultSpec(DSBOXConfig dsboxConfig) {
        DockerDriverSpec defaultSpec = new DockerDriverSpec();
        // TODO: valores iniciales por defecto del DockerDriver

        return defaultSpec;
    }

    public static final DockerDriverSpec loadFromFile(String filename) throws DSBOXException {
        try {
            JAXBContext context = JAXBContext.newInstance(DockerDriverSpec.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            DockerDriverSpec spec = (DockerDriverSpec) unmarshaller.unmarshal(new File(filename));
            return spec;
        } catch (JAXBException ex) {
            throw new DSBOXException("Error loading DockerDriverSpec from " + filename, ex);
        }
    }

    public static final void saveToFile(DockerDriverSpec spec, String filename) throws DSBOXException {
        try {
            JAXBContext context = JAXBContext.newInstance(DockerDriverSpec.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(spec, new File(filename));
        } catch (JAXBException ex) {
            throw new DSBOXException("Error saving DockerDriverSpec to " + filename, ex);
        }
    }
}
