package es.uvigo.esei.dsbox.virtualbox.execution;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.execution.SSHSessionSpec;
import es.uvigo.esei.dsbox.core.model.execution.SessionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.virtualbox_5_0.IConsole;


@XmlType(propOrder = {"status", "vboxInternalMachineName", "vboxUUID", "basePath"})
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualBoxMachine extends VirtualMachine {

    @XmlAttribute(name = "status")
    private VBoxStatus status;

    @XmlElement(name = "internal-machine-name")
    private String vboxInternalMachineName;

    @XmlElement(name = "vbox-uuid")
    private String vboxUUID;

    @XmlElement(name = "base-path")
    private String basePath;
    
    @XmlTransient
    private IConsole controlConsole;

    // Pendiente
    // - Info de snapshot actual
    // - Info de las imágenes usadas ¿? (clase VBoxDisk con "position", "uuid", "path", "modo", "path-padre")
    // - Lista de VBoxConnection con VBoxNetwork ¿es necesario? (podria llevar las MAC si se ponen "a mano")
    // - Revisar constructores
    public VirtualBoxMachine() {
        super(VMType.VIRTUALBOX);
        this.status = VBoxStatus.PAUSED;
    }

    public VirtualBoxMachine(String vboxInternalMachineName) {
        super(VMType.VIRTUALBOX);
        this.status = VBoxStatus.PAUSED;
        this.vboxInternalMachineName = vboxInternalMachineName;
    }

    public VirtualBoxMachine(String vboxInternalMachineName, Host node, String basePath) {
        super(VMType.VIRTUALBOX, node);
        this.basePath = basePath;
        this.status = VBoxStatus.PAUSED;
        this.vboxInternalMachineName = vboxInternalMachineName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getVboxUUID() {
        return vboxUUID;
    }

    public void setVboxUUID(String vboxUUID) {
        this.vboxUUID = vboxUUID;
    }

    
    
    public VBoxStatus getStatus() {
        return status;
    }

    public void setStatus(VBoxStatus status) {
        this.status = status;
    }

    public String getVboxInternalMachineName() {
        return vboxInternalMachineName;
    }

    public void setVboxInternalMachineName(String vboxInternalMachineName) {
        this.vboxInternalMachineName = vboxInternalMachineName;
    }

    public IConsole getControlConsole() {
        return controlConsole;
    }

    public void setControlConsole(IConsole controlConsole) {
        this.controlConsole = controlConsole;
    }

    @Override
    public SessionSpec getSessionSpec() {
        // TODO:  ajustar para recuparar esta info de la imagen
        return new SSHSessionSpec("none", 0, "none", "none");
    }

    
    
    @XmlEnum(String.class)
    public static enum VBoxStatus {

        PAUSED, RUNNING, SAVED, NON_STARTED
    }

}
