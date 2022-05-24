package es.uvigo.esei.dsbox.virtualbox.execution;

import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.execution.VirtualNetwork;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"vboxNetworkName"})
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualBoxNetwork extends VirtualNetwork {
    // ¿que incluir aquí?
    @XmlElement(name = "vbox-network-name")
    private String vboxNetworkName;

    public VirtualBoxNetwork(String vboxNetworkName) {
        super(VMType.VIRTUALBOX);
        this.vboxNetworkName = vboxNetworkName;
    }

    public String getVboxNetworkName() {
        return vboxNetworkName;
    }

    public void setVboxNetworkName(String vboxNetworkName) {
        this.vboxNetworkName = vboxNetworkName;
    }
 
    
}
