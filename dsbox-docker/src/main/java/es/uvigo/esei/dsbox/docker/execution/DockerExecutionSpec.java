package es.uvigo.esei.dsbox.docker.execution;

import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DockerExecutionSpec extends ExecutionSpec {

    public DockerExecutionSpec() {
        super(new DockerDriverSpec());
    }

    public DockerExecutionSpec(DockerDriverSpec driverSpec) {
        super(driverSpec);
    }

    // Faltan cosas, ¿cuales?
}
