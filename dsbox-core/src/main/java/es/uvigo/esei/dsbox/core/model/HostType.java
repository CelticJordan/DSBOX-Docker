package es.uvigo.esei.dsbox.core.model;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"id", "name", "arch", "os", "creationDate", 
                      "description", "iconURL", "vmImage", 
                      "defaultRAM", "defaultCPU", "maxInterfaces",
                      "networkAutoconfigureSupported", "servicesAutostartSupported", 
                      "useCDDVD", "CDDVDspec", "useCommonSwap", "useCommonSharedFolder"})
@XmlRootElement(name="host-type")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostType {
    private static int DEFAULT_MAX_INTERFACES = 4;
    private static int DEFAULT_RAM = 128;
    private static int DEFAULT_CPU = 100;

    @XmlID
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String name;

    private String description;
    
    @XmlAttribute
    private ArchitectureType arch;
    
    @XmlAttribute
    private OperativeSystemType os;
    
    
    @XmlElement(name = "icon-url")
    private String iconURL;

    @XmlAttribute(name = "creation-date")
    private Date creationDate;

    @XmlElement(name="vm-image")
    private VMImage vmImage;
    
    @XmlElement(name = "network-autoconfigure")
    private boolean networkAutoconfigureSupported;

    @XmlElement(name = "services-autostart")
    private boolean servicesAutostartSupported;

    @XmlElement(name="max-interfaces")
    private int maxInterfaces;
    
    @XmlElement(name="default-ram")
    private int defaultRAM;
    
    @XmlElement(name="default-cpu")
    private int defaultCPU;
    
    @XmlElement(name="use-cd-dvd")
    private boolean useCDDVD;

    @XmlElement(name="cd-dvd-spec", required = false)
    private CDDVDSpec CDDVDspec;
    
    @XmlElement(name="use-common-swap", required = false)
    private boolean useCommonSwap;
    
    @XmlElement(name="use-common-shared-folder", required = false)
    private boolean useCommonSharedFolder;
    
    // TODO: si se usa este XML para la definición de tipos de nodos, incluir el path al Icono
    // private String iconPath;
    
    public HostType() {
        this.creationDate = Calendar.getInstance().getTime();  
        this.arch = ArchitectureType.X86;
        this.os = OperativeSystemType.GNU_LINUX;
        this.networkAutoconfigureSupported = true;
        this.servicesAutostartSupported = true;
        this.maxInterfaces = DEFAULT_MAX_INTERFACES;
        this.defaultRAM = DEFAULT_RAM;
        this.defaultCPU = DEFAULT_CPU;
        this.useCDDVD = false;
        this.useCommonSwap = true;
        this.useCommonSharedFolder = false;
    }

    public HostType(String id, String name, String description, VMImage vmImage) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.vmImage = vmImage;        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArchitectureType getArch() {
        return arch;
    }

    public void setArch(ArchitectureType arch) {
        this.arch = arch;
    }

    public OperativeSystemType getOs() {
        return os;
    }

    public void setOs(OperativeSystemType os) {
        this.os = os;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public VMImage getVmImage() {
        return vmImage;
    }

    public void setVmImage(VMImage vmImage) {
        this.vmImage = vmImage;
    }

    public boolean isNetworkAutoconfigureSupported() {
        return networkAutoconfigureSupported;
    }

    public void setNetworkAutoconfigureSupported(boolean networkAutoconfigureSupported) {
        this.networkAutoconfigureSupported = networkAutoconfigureSupported;
    }

    public boolean isServicesAutostartSupported() {
        return servicesAutostartSupported;
    }

    public void setServicesAutostartSupported(boolean servicesAutostartSupported) {
        this.servicesAutostartSupported = servicesAutostartSupported;
    }

    public int getMaxInterfaces() {
        return maxInterfaces;
    }

    public void setMaxInterfaces(int maxInterfaces) {
        this.maxInterfaces = maxInterfaces;
    }

    public int getDefaultRAM() {
        return defaultRAM;
    }

    public void setDefaultRAM(int defaultRAM) {
        this.defaultRAM = defaultRAM;
    }

    public int getDefaultCPU() {
        return defaultCPU;
    }

    public void setDefaultCPU(int defaultCPU) {
        this.defaultCPU = defaultCPU;
    }

    public boolean isUseCDDVD() {
        return useCDDVD;
    }

    public void setUseCDDVD(boolean useCDDVD) {
        this.useCDDVD = useCDDVD;
        if (! useCDDVD) {
            this.CDDVDspec = null;
        }
    }

    public CDDVDSpec getCDDVDspec() {
        return CDDVDspec;
    }

    public void setCDDVDspec(CDDVDSpec CDDVDspec) {
        this.CDDVDspec = CDDVDspec;
    }



    public boolean isUseCommonSwap() {
        return useCommonSwap;
    }

    public void setUseCommonSwap(boolean useCommonSwap) {
        this.useCommonSwap = useCommonSwap;
    }

    public boolean isUseCommonSharedFolder() {
        return useCommonSharedFolder;
    }

    public void setUseCommonSharedFolder(boolean useCommonSharedFolder) {
        this.useCommonSharedFolder = useCommonSharedFolder;
    }

    


    


}
