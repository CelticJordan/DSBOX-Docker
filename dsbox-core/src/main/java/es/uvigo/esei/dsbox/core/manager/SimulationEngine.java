package es.uvigo.esei.dsbox.core.manager;

import es.uvigo.esei.dsbox.core.model.NetworkSpec;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;

public interface SimulationEngine {
    //
    public void addVMDriver(VMDriver vmDriver);    
    
    // 
    public void initialize() throws VMDriverException;
    public void finalize() throws VMDriverException;    
    
    // 
    public boolean isHostTypeRegistered(HostType hostType) throws VMDriverException;    
    public void registerHostType(HostType hostType) throws VMDriverException;
    public void unregisterHostType(HostType hostType) throws VMDriverException;

    // 
    public ExecutionSpec initSimulation(String simulationName, NetworkSpec networkSpec) throws VMDriverException;
    public ExecutionSpec startSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException;
    public ExecutionSpec pauseSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException;
    public ExecutionSpec resumeSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException;
    public ExecutionSpec stopSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException;
    public void removeSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException;
    
    //
    public void exportSimulation(String simulationName, ExecutionSpec executionSpec, String destinationPath) throws VMDriverException;
    public ExecutionSpec importSimulation(String simulationName, String sourcePath, NetworkSpec networkSpec) throws VMDriverException;    
}
