package es.uvigo.esei.dsbox.gui.controllers;

import es.uvigo.esei.dsbox.core.config.DSBOXConfig;
import es.uvigo.esei.dsbox.core.manager.VMDriver;
import es.uvigo.esei.dsbox.core.model.VMType;
import es.uvigo.esei.dsbox.core.model.exceptions.DSBOXException;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.docker.execution.DockerDriverSpec;
import es.uvigo.esei.dsbox.docker.gui.DockerDriverSpecEditorPanel;
import es.uvigo.esei.dsbox.docker.gui.connection.DockerConnectionManager;
import es.uvigo.esei.dsbox.docker.manager.DockerDriver;
import es.uvigo.esei.dsbox.core.gui.connection.ConnectionManager;
import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxDriverSpec;
import es.uvigo.esei.dsbox.virtualbox.gui.VBoxDriverSpecEditorPanel;
import es.uvigo.esei.dsbox.virtualbox.manager.VirtualBoxDriver;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.stage.Stage;

// TODO: mejorar esto ¿con plugins: PF4J?
public class Factory {

    public final static VMDriver createVMDriver(DSBOXConfig config) {
        VMType vmType = config.getVmType();

        if (vmType.equals(VMType.DOCKER)) {
            return new DockerDriver();
        }

        return new VirtualBoxDriver();
    }

    public final static VMDriverSpec createVMDriverSpec(DSBOXConfig config) {
        VMType vmType = config.getVmType();

        if (vmType.equals(VMType.DOCKER)) {
            return createDockerDriverSpec(config);
        }

        return createVirtualBoxDriverSpec(config);
    }

    private static VMDriverSpec createDockerDriverSpec(DSBOXConfig config) {
        DockerDriverSpec spec = null;
        File dockerDriverSpecFile = new File(config.getConfigDir(), DockerDriverSpec.DOCKERDRIVER_CONFIG_FILE);
        if (dockerDriverSpecFile.exists()) {
            try {
                spec = DockerDriverSpec.loadFromFile(dockerDriverSpecFile.getAbsolutePath());
            } catch (DSBOXException ex) {
                Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, "Unable to load DockerDriver config from " + dockerDriverSpecFile.getAbsolutePath(), ex);
            }
        }
        if (spec == null) {
            spec = DockerDriverSpec.createDefaultSpec(config);
            try {
                DockerDriverSpec.saveToFile(spec, dockerDriverSpecFile.getAbsolutePath());
            } catch (DSBOXException ex) {
                Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, "Unable to load DockerDriver config from " + dockerDriverSpecFile.getAbsolutePath(), ex);
            }
        }

        return spec;
    }

    private static VMDriverSpec createVirtualBoxDriverSpec(DSBOXConfig config) {
        VirtualBoxDriverSpec spec = null;
        File vboxDriverSpecFile = new File(config.getConfigDir(), VirtualBoxDriverSpec.VBOXDRIVER_CONFIG_FILE);
        if (vboxDriverSpecFile.exists()) {
            try {
                spec = VirtualBoxDriverSpec.loadFromFile(vboxDriverSpecFile.getAbsolutePath());
            } catch (DSBOXException ex) {
                Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, "Unable to load VirtualBoxDriver config from " + vboxDriverSpecFile.getAbsolutePath(), ex);
            }
        }
        if (spec == null) {
            spec = VirtualBoxDriverSpec.createDefaultSpec(config);
            try {
                VirtualBoxDriverSpec.saveToFile(spec, vboxDriverSpecFile.getAbsolutePath());
            } catch (DSBOXException ex) {
                Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, "Unable to load VirtualBoxDriver config from " + vboxDriverSpecFile.getAbsolutePath(), ex);
            }
        }
        return spec;
    }

    public static Parent createVMDriverSpecPanel(VMDriverSpec spec, Stage stage) throws DSBOXException {
        if (spec instanceof DockerDriverSpec) {
            return new DockerDriverSpecEditorPanel((DockerDriverSpec) spec, stage);
        } else if (spec instanceof VirtualBoxDriverSpec) {
            return new VBoxDriverSpecEditorPanel((VirtualBoxDriverSpec) spec, stage);
        } else {
            throw new DSBOXException("Wrong VMDriverSpec");
        }

    }
    
    public static ConnectionManager createConnectionManager(ExecutionSpec executionSpec) throws DSBOXException{
        if (executionSpec.getVmDriverSpec() instanceof DockerDriverSpec){
            return new DockerConnectionManager(executionSpec, executionSpec.getVmDriverSpec());
        } else {
            throw new DSBOXException("Unable to create connection manager");
        }
    }
}