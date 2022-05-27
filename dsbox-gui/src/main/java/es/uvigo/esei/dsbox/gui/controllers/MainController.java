package es.uvigo.esei.dsbox.gui.controllers;

import es.uvigo.esei.dsbox.core.config.DSBOXConfig;
import es.uvigo.esei.dsbox.core.manager.SimulationEngine;
import es.uvigo.esei.dsbox.core.manager.SingleInstanceSimulationEngine;
import es.uvigo.esei.dsbox.core.manager.VMDriver;
import es.uvigo.esei.dsbox.core.manager.VMDriverException;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.SimulationSpec;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.core.xml.DSBOXXMLDAO;
import es.uvigo.esei.dsbox.gui.views.MainWindow;
import java.io.File;
import java.util.List;

public class MainController {
    
    private String workingDirectory;
    private MainWindow mainWindow;
    private DSBOXConfig dsboxConfig;
    private DSBOXXMLDAO dao;
    private SimulationEngine engine;
    private VMDriverSpec VMDriverSpec;
    private VMDriver VMDriver;
    private List<HostType> registeredHostTypes;
    
    private boolean simulationIsRunning = false;
    private String runningSimulationName = null;
    private ExecutionSpec executionSpec = null;
    
    public MainController() {
    }
    
    public MainController(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.dao = new DSBOXXMLDAO();
        // TODO: generalizar tipo de VM por defecto
        this.dsboxConfig = new DSBOXConfig(VMType.DOCKER, workingDirectory, dao);
    }
    
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.dsboxConfig = new DSBOXConfig(this.dsboxConfig.getVmType(), workingDirectory, dao);
    }
    
    public String getWorkingDirectory() {
        return workingDirectory;
    }
    
    public void startUp() throws DSBOXException, VMDriverException {
        this.registeredHostTypes = dsboxConfig.getRegisteredHostTypes();

        /*************
        // Create/load VBox driver Spec
        File vboxDriverSpecFile = new File(dsboxConfig.getConfigDir(), VirtualBoxDriverSpec.VBOXDRIVER_CONFIG_FILE);
        if (vboxDriverSpecFile.exists()) {
            VMDriverSpec = VirtualBoxDriverSpec.loadFromFile(vboxDriverSpecFile.getAbsolutePath());
        }
        else {
            VMDriverSpec = VirtualBoxDriverSpec.createDefaultSpec(dsboxConfig);
            VirtualBoxDriverSpec.saveToFile(VMDriverSpec, vboxDriverSpecFile.getAbsolutePath());
        }

        VMDriver = new VirtualBoxDriver();
        VMDriver.setVMDriverSpec(VMDriverSpec);
        *******/
        
        this.VMDriver = Factory.createVMDriver(this.dsboxConfig);        
        dao.addKnownJAXBClasses(VMDriver.exposeJAXBClasses());
        
        this.VMDriverSpec = Factory.createVMDriverSpec(this.dsboxConfig);
        this.VMDriver.setVMDriverSpec(VMDriverSpec);
        
        
        engine = new SingleInstanceSimulationEngine();
        engine.addVMDriver(VMDriver);
        engine.initialize();
        
        /* TODO: en simulacion
        for (HostType hostType : registeredHostTypes) {
            if (!engine.isHostTypeRegistered(hostType)) {
                engine.registerHostType(hostType);
            }
        }
        */
    }
    
    public void shutDown() throws VMDriverException {
        if (simulationIsRunning && (executionSpec != null)) {
            engine.stopSimulation(runningSimulationName, executionSpec);
        }
        engine.finalize();
    }
    
    public boolean isValidConfiguration() {
        return dsboxConfig.checkDSBOXConfiguration();
    }
    
    public void createWorkingDirectory() throws DSBOXException {
        dsboxConfig.initializeDSBOXConfig();
    }
    
    public List<HostType> getRegisteredHostTypes() {
        return this.dsboxConfig.getRegisteredHostTypes();
    }
    
    public void registerNewHostType(HostType newHostType) throws DSBOXException, VMDriverException {
        dsboxConfig.addHostType(newHostType);
        if (!engine.isHostTypeRegistered(newHostType)) {
            engine.registerHostType(newHostType);
        }
    }
    
    public void unRegisterHostType(HostType hostType) throws DSBOXException, VMDriverException {
        dsboxConfig.removeHostType(hostType);
        engine.unregisterHostType(hostType);
    }
    
    public void saveSimulationSpec(SimulationSpec simulationSpec, String simulationSpecFilename) throws DSBOXException {
        // Set simulation dir
        // TODO better update on first start 
        File simulationDir = new File(dsboxConfig.getSimulationsDir(), simulationSpec.getName().replace(" ", "_"));
        simulationSpec.setSimulationDir(simulationDir.getAbsolutePath());
        
        dao.saveSimulationSpecToFile(simulationSpecFilename, simulationSpec);
    }
    
    public SimulationSpec loadSimulationSpec(String simulationSpecFilename) throws DSBOXException {
        return dao.loadSimulationSpecFromFile(simulationSpecFilename);
    }
    
    public void startSimulation(SimulationSpec simulationSpec) throws VMDriverException {
        String simulationName = simulationSpec.getName();
        
        // Check host type are dowloaded and registered in VMDriver
        // Control download -> cancel download => abort startSimulation
        
        executionSpec = engine.initSimulation(simulationName, simulationSpec.getNetworkSpec());
        executionSpec.setVmDriverSpec(this.VMDriverSpec);
        engine.startSimulation(simulationName, executionSpec);
        runningSimulationName = simulationName;
        simulationIsRunning = true;
    }
    
    public void pauseSimulation(SimulationSpec simulationSpec) throws VMDriverException {
        if (simulationIsRunning && (executionSpec != null)) {
            executionSpec = engine.pauseSimulation(runningSimulationName, executionSpec);
        }
    }
    
    public void resumeSimulation(SimulationSpec simulationSpec) throws VMDriverException {
        if (simulationIsRunning && (executionSpec != null)) {
            executionSpec = engine.resumeSimulation(runningSimulationName, executionSpec);
        }
    }
    
    public void stopSimulation(SimulationSpec simulationSpec) throws VMDriverException {
        if (simulationIsRunning && (executionSpec != null)) {
            executionSpec = engine.stopSimulation(runningSimulationName, executionSpec);
            simulationIsRunning = false;
            runningSimulationName = "";
            executionSpec = null;
        }
    }
    
    public String getImagesDirectory() {
        return dsboxConfig.getImagesDir().getAbsolutePath();
    }
    
    public String getSimulationsDirectory() {
        return dsboxConfig.getSimulationsDir().getAbsolutePath();
    }

    public VMDriverSpec getVMDriverSpec() {
        return this.VMDriverSpec;
    }
    
    public ExecutionSpec getExecutionSpec(){
        if (simulationIsRunning){
            return this.executionSpec;            
        }
        return null;
    }
    
}
