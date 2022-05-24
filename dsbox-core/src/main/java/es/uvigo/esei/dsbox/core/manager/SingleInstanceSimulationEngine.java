package es.uvigo.esei.dsbox.core.manager;

import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.core.model.NetworkSpec;
import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.HostType;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VirtualMachine;
import es.uvigo.esei.dsbox.core.model.execution.VirtualNetwork;

public class SingleInstanceSimulationEngine implements SimulationEngine {

    private VMDriver vmDriver;

    @Override
    public void addVMDriver(VMDriver vmDriver) {
        this.vmDriver = vmDriver;
    }

    @Override
    public void initialize() throws VMDriverException {
        checkVMDriver();
        this.vmDriver.initializeVMDriver();
    }

    @Override
    public void finalize() throws VMDriverException {
        checkVMDriver();
        // ¿guardar estado simulacion antes de finalizar?
        this.vmDriver.finalizeVMDriver();

    }

    public void registerHostType(HostType hostType) throws VMDriverException {
        checkVMDriver();
        this.vmDriver.registerHostType(hostType);
    }

    public void unregisterHostType(HostType hostType) throws VMDriverException {
        checkVMDriver();
        this.vmDriver.unregisterHostType(hostType);
    }

    @Override
    public ExecutionSpec initSimulation(String simulationName, NetworkSpec networkSpec) throws VMDriverException {
        checkVMDriver();

        ExecutionSpec executionSpec = new ExecutionSpec();
        // recorrer tipos de nodo, registrandolos si no lo están ya (igual no es necesario, ya se habría registrado al dar de alta el tipo de nodo)
        //for (NodeType nodeType : networkSpec.getNodeTypes()) {
        //    this.vmDriver.registerNodeType(nodeType);
        //}

        // recorrer redes, creandolas si corresponde
        if (networkSpec.getNetworks() != null) {
            for (Network network : networkSpec.getNetworks()) {
                VirtualNetwork virtualNetwork = this.vmDriver.createVirtualNetwork(simulationName, network);
                executionSpec.addVirtualNetwork(virtualNetwork);
            }
        }
        // recorrer nodos, creandolo si corresponde (o recuperando ejecución anterior)
        if (networkSpec.getHosts() != null) {
            for (Host host : networkSpec.getHosts()) {
                VirtualMachine virtualMachine = this.vmDriver.createVirtualMachine(simulationName, host);
                executionSpec.addVirtualMachine(virtualMachine);
            }
        }

        return executionSpec;
    }

    @Override
    public ExecutionSpec startSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException {
        checkVMDriver();

        ExecutionSpec newExecutionSpec = new ExecutionSpec();
        newExecutionSpec.setVirtualNetworks(executionSpec.getVirtualNetworks());  // no hay cambios en VirtualNetworks

        // recorrer VirtualMAchines, y pedir al VMDriver que las inicie        
        for (VirtualMachine virtualMachine : executionSpec.getVirtualMachines()) {
            VirtualMachine runninVirtualMachine = this.vmDriver.startVirtualMachine(simulationName, virtualMachine);
            // ¿hace falta guardar esa runningVirtualMachine?
            newExecutionSpec.addVirtualMachine(runninVirtualMachine);
        }
        return newExecutionSpec;
    }

    @Override
    public ExecutionSpec pauseSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException {
        checkVMDriver();

        ExecutionSpec newExecutionSpec = new ExecutionSpec();
        newExecutionSpec.setVirtualNetworks(executionSpec.getVirtualNetworks());  // no hay cambios en VirtualNetworks

        // recorrer VirtualMAchines, y pedir al VMDriver que las pare
        for (VirtualMachine virtualMachine : executionSpec.getVirtualMachines()) {
            VirtualMachine stoppedVirtualMachine = this.vmDriver.pauseVirtualMachine(simulationName, virtualMachine);
            // ¿hace falta guardar esa VirtualMachine?
            newExecutionSpec.addVirtualMachine(stoppedVirtualMachine);
        }
        return newExecutionSpec;
    }

    @Override
    public ExecutionSpec resumeSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException {
        checkVMDriver();

        ExecutionSpec newExecutionSpec = new ExecutionSpec();
        newExecutionSpec.setVirtualNetworks(executionSpec.getVirtualNetworks());  // no hay cambios en VirtualNetworks
        // recorrer VirtualMAchines, y, si estan paradas, pedir al VMDriver que las reanude
        for (VirtualMachine virtualMachine : executionSpec.getVirtualMachines()) {

            // TODO : ¿comprobar que esta parada?
            VirtualMachine resumedVirtualMachine = this.vmDriver.resumeVirtualMachine(simulationName, virtualMachine);
            // ¿hace falta guardar esa VirtualMachine?
            newExecutionSpec.addVirtualMachine(resumedVirtualMachine);
        }
        return newExecutionSpec;
    }

    @Override
    public ExecutionSpec stopSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException {
        checkVMDriver();

        ExecutionSpec newExecutionSpec = new ExecutionSpec();
        //newExecutionSpec.setVirtualNetworks(executionSpec.getVirtualNetworks());  // no hay cambios en VirtualNetworks
        // recorrer VirtualMAchines, y pedir al VMDriver que las finalice
        for (VirtualMachine virtualMachine : executionSpec.getVirtualMachines()) {
            this.vmDriver.removeVirtualMachine(simulationName, virtualMachine);
            // ¿hace falta guardar esa VirtualMachine?
            //newExecutionSpec.addVirtualMachine(stoppedVirtualMachine);
        }

        for (VirtualNetwork virtualNetwork : executionSpec.getVirtualNetworks()) {
            this.vmDriver.removeVirtualNetwork(simulationName, virtualNetwork);
            // ¿hace falta guardar esa VirtualMachine?
            //newExecutionSpec.addVirtualNetwork(virtualNetwork);
        }
        
        
        return newExecutionSpec;
    }

    @Override
    public void removeSimulation(String simulationName, ExecutionSpec executionSpec) throws VMDriverException {
        checkVMDriver();

        // TODO ¿comprobar que la simulacion esta parada?
        // recorrer VirtualNetworks, y pedir al VMDriver que las elimine
        for (VirtualNetwork virtualNetwork : executionSpec.getVirtualNetworks()) {
            this.vmDriver.removeVirtualNetwork(simulationName, virtualNetwork);
        }

        // recorrer VirtualMAchines, y pedir al VMDriver que las elimine
        for (VirtualMachine virtualMachine : executionSpec.getVirtualMachines()) {
            this.vmDriver.removeVirtualMachine(simulationName, virtualMachine);
        }
    }

    @Override
    public void exportSimulation(String simulationName, ExecutionSpec executionSpec, String destinationPath) {
        // TODO pendiente
    }

    @Override
    public ExecutionSpec importSimulation(String simulationName, String sourcePath, NetworkSpec networkSpec) {
        // TODO pendiente
        return null;
    }

    /**
     * Comprueba que el VMDriver exista y sea correcto ¿? Si algo falla, lanza
     * una excepcion
     *
     * @throws VMDriverException
     */
    private void checkVMDriver() throws VMDriverException {
        if (this.vmDriver == null) {
            throw new VMDriverException("VMDriver not set");
        }
        // TODO ¿otras comprobaciones?
    }

    @Override
    public boolean isHostTypeRegistered(HostType hostType) throws VMDriverException {
        checkVMDriver();
        return this.vmDriver.isHostTypeRegistered(hostType);
    }

}
