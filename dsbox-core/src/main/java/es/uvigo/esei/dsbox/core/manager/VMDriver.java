package es.uvigo.esei.dsbox.core.manager;

import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import es.uvigo.esei.dsbox.core.model.execution.VirtualNetwork;

public interface VMDriver {
    //
    public void setVMDriverSpec(VMDriverSpec vmDriverSpec);
    public void initializeVMDriver() throws VMDriverException;
    public void finalizeVMDriver() throws VMDriverException;

    // 
    public boolean isHostTypeRegistered(HostType hostType) throws VMDriverException;    
    public void registerHostType(HostType hostType) throws VMDriverException;
    public void unregisterHostType(HostType hostType) throws VMDriverException;

    //
    public VirtualNetwork createVirtualNetwork(String simulationName, Network network) throws VMDriverException;
    public VirtualNetwork updateVirtualNetwork(String simulationName, VirtualNetwork virtualNetwork, Network newNtwork) throws VMDriverException;
    public void removeVirtualNetwork(String simulationName, VirtualNetwork virtualNetwork) throws VMDriverException;

    //
    public VirtualMachine createVirtualMachine(String simulationName, Host host) throws VMDriverException;
    public VirtualMachine updateVirtualMachine(String simulationName, VirtualMachine virtualMachine, Host newHost) throws VMDriverException;
    public void removeVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException;

    //
    public VirtualMachine startVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException;
    public VirtualMachine pauseVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException;
    public VirtualMachine resumeVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException;
    public VirtualMachine stopVirtualMachine(String simulationName, VirtualMachine virtualMachine) throws VMDriverException;

    // 
    public void exportVirtualMachine(String simulationName, VirtualMachine virtualMachine, String destinationPath) throws VMDriverException;
    public VirtualMachine importVirtualMachine(String simulationName, String sourcePath, Host node) throws VMDriverException;
    
    public Class[] exposeJAXBClasses();
    
}
