package es.uvigo.esei.dsbox.core.model;

import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.graphical.GraphicalSpec;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "simulation-spec")
// TODO: no está resuelto el mapeo de "executionSpec" -> falta definir cosas y resolver el mapeo de la herencia
//@XmlType(propOrder = {"name", "creationDate", "description", "author",
//                      "networkSpec", "executionSpec", "graphicalSpec"})
@XmlType(propOrder = {"name", "creationDate", "description", "author", "simulationDir",
    "networkSpec", "graphicalSpec"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SimulationSpec {

    @XmlAttribute
    private String name;

    @XmlAttribute(name = "creation-date")
    private Date creationDate;

    private String description;

    private String author;

    @XmlAttribute(name = "simulation-dir")
    private String simulationDir;

    @XmlElement(name = "network-spec")
    private NetworkSpec networkSpec;

    // TODO: no está resuelto el mapeo de "executionSpec" -> falta definir cosas y resolver el mapeo de la herencia
    //@XmlElement(name = "execution-spec", required = false)        
    //private ExecutionSpec executionSpec;
    @XmlElement(name = "graphical-spec", required = false)
    private GraphicalSpec graphicalSpec;

    public SimulationSpec() {
        this.name = "DSBOX";
        this.description = "DSBOX network";
        this.author = "DSBOX";
        this.creationDate = Calendar.getInstance().getTime();        
    }

    public SimulationSpec(String name, String description, String author) {
        this.name = name;
        this.creationDate = Calendar.getInstance().getTime();
        this.description = description;
        this.author = author;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSimulationDir() {
        return simulationDir;
    }

    public void setSimulationDir(String simulationDir) {
        this.simulationDir = simulationDir;
    }
    
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NetworkSpec getNetworkSpec() {
        return networkSpec;
    }

    public void setNetworkSpec(NetworkSpec networkSpec) {
        this.networkSpec = networkSpec;
    }

    // TODO: no está resuelto el mapeo de "executionSpec" -> falta definir cosas y resolver el mapeo de la herencia
    //public ExecutionSpec getExecutionSpec() {
    //    return executionSpec;
    //}
    // TODO: no está resuelto el mapeo de "executionSpec" -> falta definir cosas y resolver el mapeo de la herencia
    //public void setExecutionSpec(ExecutionSpec executionSpec) {
    //    this.executionSpec = executionSpec;
    //}
    public GraphicalSpec getGraphicalSpec() {
        return graphicalSpec;
    }

    public void setGraphicalSpec(GraphicalSpec graphicalSpec) {
        this.graphicalSpec = graphicalSpec;
    }

}
