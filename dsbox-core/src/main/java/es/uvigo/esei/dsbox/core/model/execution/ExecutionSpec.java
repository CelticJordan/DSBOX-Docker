package es.uvigo.esei.dsbox.core.model.execution;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"vmDriverSpec", "virtualMachines", "virtualNetworks"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionSpec {
    @XmlElement(name="vm-driver-spec")
    private VMDriverSpec vmDriverSpec;
    
    @XmlElementWrapper(name = "virtual-machines")
    @XmlElement(name = "virtual-machine")    
    private List<VirtualMachine> virtualMachines;
    
    @XmlElementWrapper(name = "virtual-networks")
    @XmlElement(name = "virtual-network")    
    private List<VirtualNetwork> virtualNetworks;

    public ExecutionSpec() {
    }

    public ExecutionSpec(VMDriverSpec vmDriverSpec) {
        this.vmDriverSpec = vmDriverSpec;
    }

    public VMDriverSpec getVmDriverSpec() {
        return vmDriverSpec;
    }

    public void setVmDriverSpec(VMDriverSpec vmDriverSpec) {
        this.vmDriverSpec = vmDriverSpec;
    }

    public List<VirtualMachine> getVirtualMachines() {
        return virtualMachines;
    }

    public void setVirtualMachines(List<VirtualMachine> virtualMachines) {
        this.virtualMachines = virtualMachines;
    }

    public List<VirtualNetwork> getVirtualNetworks() {
        return virtualNetworks;
    }

    public void setVirtualNetworks(List<VirtualNetwork> virtualNetworks) {
        this.virtualNetworks = virtualNetworks;
    }
    
    
    public void addVirtualMachine(VirtualMachine virtualMachine) {
        if (this.virtualMachines == null) {
            this.virtualMachines = new ArrayList<VirtualMachine>();
        }
        this.virtualMachines.add(virtualMachine);
    }

    public void removeVirtualMachine(VirtualMachine virtualMachine) {
        if (this.virtualMachines != null) {
            this.virtualMachines.remove(virtualMachine);
        }
    }    

    public void addVirtualNetwork(VirtualNetwork virtualNetwork) {
        if (this.virtualNetworks == null) {
            this.virtualNetworks = new ArrayList<VirtualNetwork>();
        }
        this.virtualNetworks.add(virtualNetwork);
    }

    public void removeVirtualNetwork(VirtualNetwork virtualNetwork) {
        if (this.virtualNetworks != null) {
            this.virtualNetworks.remove(virtualNetwork);
        }
    }    
    
}
