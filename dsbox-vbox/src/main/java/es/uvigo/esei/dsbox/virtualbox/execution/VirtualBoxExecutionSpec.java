package es.uvigo.esei.dsbox.virtualbox.execution;

import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualBoxExecutionSpec extends ExecutionSpec {

    public VirtualBoxExecutionSpec() {
        super(new VirtualBoxDriverSpec());
    }

    public VirtualBoxExecutionSpec(VirtualBoxDriverSpec vmDriverSpec) {
        super(vmDriverSpec);
    }
   
    // Faltan cosas, ¿cuales?

    
    
}
