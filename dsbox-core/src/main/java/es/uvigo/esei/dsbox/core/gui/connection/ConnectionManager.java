/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.uvigo.esei.dsbox.core.gui.connection;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import java.util.function.Function;
import javafx.stage.Stage;

/**
 *
 * @author jordan
 */
public abstract class ConnectionManager {
    
    private ExecutionSpec executionSpec;
    private VMDriverSpec vmDriverSpec;

    public ConnectionManager(ExecutionSpec executionSpec, VMDriverSpec vmDriverSpec) {
        this.executionSpec = executionSpec;
        this.vmDriverSpec = vmDriverSpec;
    }

    public ExecutionSpec getExecutionSpec() {
        return executionSpec;
    }

    public void setExecutionSpec(ExecutionSpec executionSpec) {
        this.executionSpec = executionSpec;
    }

    public VMDriverSpec getVmDriverSpec() {
        return vmDriverSpec;
    }

    public void setVmDriverSpec(VMDriverSpec vmDriverSpec) {
        this.vmDriverSpec = vmDriverSpec;
    }
    
    public abstract Stage createViewForHost (Host host, Runnable closeAction);
       
    
    
    
}
