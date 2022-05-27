package es.uvigo.esei.dsbox.gui.editor.events;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.config.TerminalConfig;
import com.kodedu.terminalfx.helper.IOHelper;
import com.kodedu.terminalfx.helper.ThreadHelper;
import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.execution.ExecutionSpec;
import es.uvigo.esei.dsbox.core.gui.connection.ConnectionManager;
import es.uvigo.esei.dsbox.gui.editor.HostNodeView;
import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.Position;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;
import java.util.HashMap;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimulationModeEventProcessor extends BaseNetworkEventProcessor {

    private NodeView selectedNode;
    private ConnectionManager connectionManager;
    private Parent parentWindow;
    HashMap<String, Stage> stages = new HashMap<>();

    public SimulationModeEventProcessor(NetworkView networkView, EditActionDestination actionDestination, ConnectionManager connectionManager, Parent parentWindow) {
        super(networkView, actionDestination);
        this.connectionManager = connectionManager;
        this.parentWindow = parentWindow;
    }

    @Override
    public void onNodeSelected(NetworkNodeEvent ne) {
        selectedNode = ne.getNodeView();

        selectedNode.toFront();
        selectedNode.setCursor(Cursor.CLOSED_HAND);
        selectedNode.setEffect(new DropShadow());
    }

    @Override
    public void onNodeUnselected(NetworkNodeEvent ne) {
        if (selectedNode != null) {
            selectedNode.setCursor(Cursor.DEFAULT);
            selectedNode.setEffect(null);
            selectedNode = null;
        }
    }

    private Host retrieveHost(NetworkNodeEvent ne) {
        NodeView nv = ne.nodeView;
        if (nv instanceof HostNodeView) {
            HostNodeView hnv = (HostNodeView) nv;
            return hnv.getHost();
        }
        return null;

    }

    @Override
    public void onNodeClicked(NetworkNodeEvent ne) {
        Host host = retrieveHost(ne);
        if (host != null) {
            String hostName = host.getName();
            if (!stages.containsKey(hostName)) {
//                TerminalConfig defaultConfig = new TerminalConfig();
//                defaultConfig.setUnixTerminalStarter(String.format("docker exec -it %s bash", hostName));
//
//                Terminal t = new Terminal(defaultConfig, null);
//                t.setMinSize(650, 400);
//                Scene scene = new Scene(t);
//                Stage stage = new Stage();
//                stage.setTitle(hostName);
//                stage.setScene(scene);
                Stage stage = connectionManager.createViewForHost(host, ()->{stages.remove(hostName);});
                
                
                
                stages.put(hostName, stage);
                stage.showAndWait();
                stage.toFront();
            } else {
                Stage stage = stages.get(hostName);
                if (!stage.isShowing()) {
                    stage.showAndWait();
                } else {
                    stage.toFront();
                }
            }
        }
    }

    @Override
    public void onNodeDoubleClicked(NetworkNodeEvent ne) {
    }

    @Override
    public void onNodeSecondaryClicked(NetworkNodeEvent ne) {

        if (ne.getNodeView() instanceof HostNodeView) {
            HostNodeView hostView = (HostNodeView) ne.getNodeView();

            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setHideOnEscape(true);
            contextMenu.setAutoHide(true);
            contextMenu.setAutoFix(true);
            contextMenu.setConsumeAutoHidingEvents(true);

            MenuItem status = new MenuItem("Host status");
            SeparatorMenuItem separator = new SeparatorMenuItem();
            MenuItem pause = new MenuItem("Pause");
            MenuItem stop = new MenuItem("Stop");

            status.setOnAction((ActionEvent ae) -> {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Status info. for host " + hostView.getHost().getName() + " is not available.", ButtonType.OK);
                alert.setTitle("Warning.");
                alert.setHeaderText("Warning.");
                alert.showAndWait();
            });

            pause.setDisable(true);
            stop.setDisable(true);

            contextMenu.getItems().addAll(status, separator, pause, stop);
            contextMenu.show(hostView, ne.getScreenPosition().getX(), ne.getScreenPosition().getY());
        }
    }

    @Override
    public void onLinkSecondaryClicked(NetworkLinkEvent ne) {
        LinkView link = ne.linkView;
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setHideOnEscape(true);
        contextMenu.setAutoHide(true);
        contextMenu.setAutoFix(true);

        MenuItem undo = new MenuItem("Undo");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");

        contextMenu.getItems().addAll(undo, separator, cut, copy, paste);
        Bounds linkBounds = link.getBoundsInParent();

        double menuXoffset = (ne.getPosition().getX() - networkView.getLayoutX()) - linkBounds.getMinX();
        double menuYoffset = (ne.getPosition().getY() - networkView.getLayoutY()) - linkBounds.getMinY();
        contextMenu.show(ne.getLinkView(), Side.LEFT, menuXoffset, menuYoffset);

    }

    @Override
    public void onLinkDoubleClicked(NetworkLinkEvent ne) {
        LinkView link = ne.linkView;
    }

    @Override
    public void onLinkSelected(NetworkLinkEvent ne) {
        LinkView link = ne.linkView;
    }

    @Override
    public void onLinkClicked(NetworkLinkEvent ne) {
        onLinkSelected(ne);  // TODO: check equivalence
    }

    @Override
    public void onCanvasClicked(NetworkEvent ne) {
        if (selectedNode != null) {
            selectedNode = null;
        }
    }
    
    

    private void refreshNodePosition(NodeView nv, Position newPosition) {
        networkView.moveNodeView(nv, newPosition);
    }

}
