/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.uvigo.esei.dsbox.docker.gui.connection;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.config.TerminalConfig;
import com.kodedu.terminalfx.helper.IOHelper;
import com.kodedu.terminalfx.helper.ThreadHelper;
import es.uvigo.esei.dsbox.core.gui.connection.ConnectionManager;
import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.model.execution.VMDriverSpec;
import es.uvigo.esei.dsbox.docker.execution.DockerDriverSpec;
import java.util.Objects;
import java.util.function.Function;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author jordan
 */
public class DockerConnectionManager extends ConnectionManager {

    public DockerConnectionManager(ExecutionSpec executionSpec, VMDriverSpec vmDriverSpec) {
        super(executionSpec, vmDriverSpec);
    }
    
    public Stage createViewForHost (Host host, Runnable closeAction){
        String hostName = host.getName();
        TerminalConfig defaultConfig = new TerminalConfig();
        String dockerCommand = "docker";
        if (this.getVmDriverSpec() instanceof DockerDriverSpec){
            dockerCommand = ((DockerDriverSpec) this.getVmDriverSpec()).getDockerServerHost();
        }
        defaultConfig.setUnixTerminalStarter(String.format("%s exec -it %s bash", dockerCommand, hostName));

        Terminal t = new Terminal(defaultConfig, null);
        t.setMinSize(650, 400);
        Scene scene = new Scene(t);
        Stage stage = new Stage();
        stage.setTitle(hostName);
        stage.setScene(scene);
        
        
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        closeAction.run();

                        ThreadHelper.start(() -> {
                            while (Objects.isNull(t.getProcess())) {
                                ThreadHelper.sleep(250);
                            }
                            t.getProcess().destroy();
                            IOHelper.close(t.getInputReader(), t.getErrorReader(), t.getOutputWriter());
                        });

                    }
                });
        return stage;
    }
    
    
}
