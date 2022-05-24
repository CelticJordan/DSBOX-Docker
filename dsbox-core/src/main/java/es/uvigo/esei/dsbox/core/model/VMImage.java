package es.uvigo.esei.dsbox.core.model;

import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {"imageType", "name", "creationDate", "downloaded", "compressed", "compressorType", "downloadURI", "localURI", "defaultLogin", "defaultPassword"})
@XmlAccessorType(XmlAccessType.FIELD)
public class VMImage {

    @XmlAttribute(name = "creation-date")
    private Date creationDate;

    @XmlAttribute(name = "image-type")
    private ImageType imageType;

    @XmlElement(name = "image-name")
    private String name;

    @XmlElement(name = "download-uri")
    private String downloadURI;

    @XmlElement(name = "local-uri")
    private String localURI;

    @XmlElement(name = "default-login")
    private String defaultLogin="dsbox";

    @XmlElement(name = "default-password")
    private String defaultPassword="dsbox";
    
    @XmlAttribute
    private boolean downloaded;

    @XmlAttribute(name="is-compressed")
    private boolean compressed;
    
    @XmlAttribute(name="compressor-type")
    private CompressorType compressorType;
    
    
    
    public VMImage() {
        this.creationDate = Calendar.getInstance().getTime();
        this.downloaded = false;
        this.compressorType = CompressorType.NONE;
        this.compressed = false;
    }
    
    public VMImage(ImageType imageType, String name) {
        this();       
        this.imageType = imageType;
        this.name = name;
    }

    public VMImage(ImageType imageType, String name, String downloadURI) {
        this(imageType, name);       
        this.downloadURI = downloadURI;
        if (downloadURI.endsWith(".gz")) {
           this.compressed = true;
           this.compressorType = CompressorType.GZIP;
        }
    }

    public VMImage(ImageType imageType, String name, String downloadURI, String localURI) {
        this(imageType, name, downloadURI);
        this.localURI = localURI;
        if (localURI.endsWith(".gz")) {
           this.compressed = true;
           this.compressorType = CompressorType.GZIP;
        }        
        this.downloaded = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getDownloadURI() {
        return downloadURI;
    }

    public void setDownloadURI(String downloadURI) {
        this.downloadURI = downloadURI;
    }

    public String getLocalURI() {
        return localURI;
    }

    public void setLocalURI(String localURI) {
        this.localURI = localURI;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public CompressorType getCompressorType() {
        return compressorType;
    }

    public void setCompressorType(CompressorType compressorType) {
        this.compressorType = compressorType;
        if (compressorType == CompressorType.NONE) {
            this.compressed = false;
        }
    }

    public String getDefaultLogin() {
        return defaultLogin;
    }

    public void setDefaultLogin(String defaultLogin) {
        this.defaultLogin = defaultLogin;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }
    

    @XmlEnum(String.class)
    public static enum ImageType {
        VDI, VMDK, QEMU, 
        DOCKER  // V2.0
    }
    
    @XmlEnum(String.class)
    public static enum CompressorType {
        NONE, GZIP, ZIP
    }    
}
