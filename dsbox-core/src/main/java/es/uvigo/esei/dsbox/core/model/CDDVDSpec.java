package es.uvigo.esei.dsbox.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"mode", "imagePath", "driveName"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CDDVDSpec {
    
    @XmlAttribute(name="mode")
    private CDDVDmode mode;
    
    @XmlAttribute(name="image-path", required = false)
    private String imagePath;
    
    @XmlAttribute(name="drive-name", required = false)
    private String driveName;

    public CDDVDSpec() {
    }

    public CDDVDSpec(String imagePathORdriveName) {
        if (imagePathORdriveName.endsWith(".iso") || imagePathORdriveName.endsWith(".ISO")) {
            this.mode = CDDVDmode.ISO_IMAGE;
            this.imagePath = imagePathORdriveName;
            this.driveName = null;
        }
        else {
            this.mode = CDDVDmode.PHYSICAL_DRIVE;
            this.imagePath = null;
            this.driveName = imagePathORdriveName;        
        }
    }
    
    public CDDVDSpec(CDDVDmode mode, String imagePath, String driveName) {
        this.mode = mode;
        this.imagePath = imagePath;
        this.driveName = driveName;
    }

    public CDDVDmode getMode() {
        return mode;
    }

    public void setMode(CDDVDmode mode) {
        this.mode = mode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.mode = CDDVDmode.ISO_IMAGE;
    }

    public String getDriveName() {
        return driveName;
    }

    public void setDriveName(String driveName) {
        this.driveName = driveName;
        this.mode = CDDVDmode.PHYSICAL_DRIVE;
    }
    
    
    

    public static enum CDDVDmode {
        PHYSICAL_DRIVE, 
        ISO_IMAGE
    }
    
}
