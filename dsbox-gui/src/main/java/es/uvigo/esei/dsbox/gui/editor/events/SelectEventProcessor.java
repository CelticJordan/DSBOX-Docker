package es.uvigo.esei.dsbox.gui.editor.events;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.gui.editor.HostNodeView;
import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkNodeView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.Position;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;
import es.uvigo.esei.dsbox.gui.editor.actions.MoveNodeAction;
import es.uvigo.esei.dsbox.gui.editor.actions.RemoveLinkAction;
import es.uvigo.esei.dsbox.gui.editor.actions.RemoveNodeAction;
import es.uvigo.esei.dsbox.gui.views.panels.HostEditorPanel;
import es.uvigo.esei.dsbox.gui.views.panels.LinkEditorPanel;
import es.uvigo.esei.dsbox.gui.views.panels.NetworkEditorPanel;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectEventProcessor extends BaseNetworkEventProcessor {

    private boolean snapToGrid = false;
    private int gridSize = 20;

    private Position selectedNodeInitialPosition;
    private Position selectedNodeCurrentPosition;
    private Position dragAnchor;
    private NodeView selectedNode;

    public SelectEventProcessor(NetworkView networkView, EditActionDestination actionDestination) {
        super(networkView, actionDestination);
        this.snapToGrid = networkView.isSnapToGrid();
        this.gridSize = networkView.getGridSize();
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public void onNodeSelected(NetworkNodeEvent ne) {
        selectedNode = ne.getNodeView();
        selectedNodeInitialPosition = new Position(selectedNode.getTranslateX(), selectedNode.getTranslateY());
        selectedNodeCurrentPosition = selectedNodeInitialPosition;
        dragAnchor = ne.getPosition();

        selectedNode.toFront();
        selectedNode.setCursor(Cursor.CLOSED_HAND);
        selectedNode.setEffect(new DropShadow());
    }

    @Override
    public void onNodeUnselected(NetworkNodeEvent ne) {
        if (selectedNode != null) {
            selectedNode.setCursor(Cursor.DEFAULT);
            selectedNode.setEffect(null);

            if (!selectedNodeCurrentPosition.equals(selectedNodeInitialPosition)) {
                MoveNodeAction action = new MoveNodeAction(selectedNode, selectedNodeInitialPosition, selectedNodeCurrentPosition, networkView);
                actionDestination.processEditAction(action);
            }
            selectedNode = null;

        }
    }

    @Override
    public void onNodeClicked(NetworkNodeEvent ne) {
        //onNodeSelected(ne);  // TODO: check equivalence
    }

    @Override
    public void onNodeDragged(NetworkNodeEvent ne) {
        if (selectedNode != null) {
            double dragX = ne.getPosition().getX() - dragAnchor.getX();
            double dragY = ne.getPosition().getY() - dragAnchor.getY();
            //calculate new position of the circle
            double newXPosition = selectedNodeInitialPosition.getX() + dragX;
            double newYPosition = selectedNodeInitialPosition.getY() + dragY;

            if (snapToGrid || networkView.isSnapToGrid()) {
                newXPosition = gridSize * Math.round(newXPosition / gridSize);
                newYPosition = gridSize * Math.round(newYPosition / gridSize);
            }

//        //if new position do not exceeds borders of the rectangle, translate to this position
//        if ((newXPosition > 0) && (newXPosition <= RECT_WIDTH - circle.getWidth())) {
//            circle.setTranslateX(newXPosition);
//        }
//        if ((newYPosition > 0) && (newYPosition <= RECT_HEIGHT - circle.getHeight())) {
//            circle.setTranslateY(newYPosition);
//        }
// check boundaries
            NodeView nv = ne.nodeView;
//        nv.updatePosition(ne.position);
            selectedNodeCurrentPosition = new Position(newXPosition, newYPosition);
            refreshNodePosition(nv, selectedNodeCurrentPosition);

//            nv.updatePosition(new Position(newXPosition, newYPosition));
        }
    }

    @Override
    public void onNodeDoubleClicked(NetworkNodeEvent ne) {
        if (ne.getNodeView() instanceof HostNodeView) {
            openHostEditDialog((HostNodeView) ne.getNodeView());
        } else {
            openNetworkEditDialog((NetworkNodeView) ne.getNodeView());
        }
    }

    private void openHostEditDialog(HostNodeView hostNodeView) {
        Stage stage = new Stage();
        stage.setTitle("Properties for Host");

        Host host = hostNodeView.getHost();
        HostEditorPanel editPanel = new HostEditorPanel(host);
        editPanel.setCancelActionHandler((ActionEvent ae) -> {
            stage.close();
        });
        editPanel.setSaveActionHandler((ActionEvent ae) -> {
            editPanel.updateModelValues();
            stage.close();
            hostNodeView.setLabelText(host.getName());
        });
        editPanel.setRegisteredHostTypes(networkView.getRegisteredHostTypes());

        stage.setScene(new Scene(editPanel));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(networkView.getScene().getWindow());
        stage.setResizable(false);
        stage.showAndWait();
    }

    private void openNetworkEditDialog(NetworkNodeView networkNodeView) {
        Stage stage = new Stage();
        stage.setTitle("Properties for Network");
        Network network = networkNodeView.getNetwork();
        // Collect input links
        List<Link> inputLinks = new ArrayList<>();
        for (LinkView linkView : networkNodeView.getInputLinks()) {
            inputLinks.add(linkView.getLink());
        }
        NetworkEditorPanel editPanel = new NetworkEditorPanel(network, inputLinks);
        editPanel.setCancelActionHandler((ActionEvent ae) -> {
            stage.close();
        });
        editPanel.setSaveActionHandler((ActionEvent ae) -> {
            editPanel.updateModelValues();
            stage.close();
            networkNodeView.setLabelText(network.getName());
        });

        stage.setScene(new Scene(editPanel));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(networkView.getScene().getWindow());
        stage.setResizable(false);
        stage.showAndWait();
    }

    private void openLinkEditDialog(LinkView linkView) {
        Stage stage = new Stage();
        stage.setTitle("Properties for Link");
        Link link = linkView.getLink();

        LinkEditorPanel editPanel = new LinkEditorPanel(link);
        editPanel.setCancelActionHandler((ActionEvent ae) -> {
            stage.close();
        });
        editPanel.setSaveActionHandler((ActionEvent ae) -> {
            editPanel.updateModelValues();
            stage.close();
        });

        stage.setScene(new Scene(editPanel));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(networkView.getScene().getWindow());
        stage.setResizable(false);
        stage.showAndWait();
    }

    @Override
    public void onNodeSecondaryClicked(NetworkNodeEvent ne) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setHideOnEscape(true);
        contextMenu.setAutoHide(true);
        contextMenu.setAutoFix(true);
        contextMenu.setConsumeAutoHidingEvents(true);

        MenuItem edit = new MenuItem("Edit node");
        edit.setOnAction((ActionEvent ae) -> {
            if (ne.getNodeView() instanceof HostNodeView) {
                openHostEditDialog((HostNodeView) ne.getNodeView());
            } else {
                openNetworkEditDialog((NetworkNodeView) ne.getNodeView());
            }
        });
        MenuItem remove = new MenuItem("Remove node");
        remove.setOnAction((ActionEvent ae) -> {
            RemoveNodeAction action = new RemoveNodeAction(networkView, ne.getNodeView());
            actionDestination.processEditAction(action);
        });

        contextMenu.getItems().addAll(edit, remove);
        contextMenu.show(ne.getNodeView(), ne.getScreenPosition().getX(), ne.getScreenPosition().getY());
        //contextMenu.show(ne.getNodeView(), Side.LEFT, (ne.getPosition().getX() - ne.nodeView.getTranslateX()), (ne.getPosition().getY() - ne.nodeView.getTranslateY()));
    }

    @Override
    public void onLinkSecondaryClicked(NetworkLinkEvent ne) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setHideOnEscape(true);
        contextMenu.setAutoHide(true);
        contextMenu.setAutoFix(true);
        contextMenu.setConsumeAutoHidingEvents(true);

        MenuItem edit = new MenuItem("Edit link");
        edit.setOnAction((ActionEvent ae) -> {
            openLinkEditDialog(ne.linkView);
        });
        MenuItem remove = new MenuItem("Remove link");
        remove.setOnAction((ActionEvent ae) -> {
            RemoveLinkAction action = new RemoveLinkAction(networkView, ne.getLinkView());
            actionDestination.processEditAction(action);
        });

        contextMenu.getItems().addAll(edit, remove);

//        contextMenu.getItems().addAll(undo, separator, cut, copy, paste);
//        Bounds linkBounds = link.getBoundsInParent();
//        
//        double menuXoffset = (ne.getPosition().getX() - networkView.getLayoutX()) - linkBounds.getMinX();
//        double menuYoffset = (ne.getPosition().getY() - networkView.getLayoutY()) - linkBounds.getMinY();
//        contextMenu.show(ne.getLinkView(), Side.LEFT, menuXoffset, menuYoffset);
        contextMenu.show(ne.getLinkView(), ne.getScreenPosition().getX(), ne.getScreenPosition().getY());

    }

    @Override
    public void onLinkDoubleClicked(NetworkLinkEvent ne) {
        openLinkEditDialog(ne.linkView);
    }

    @Override
    public void onLinkSelected(NetworkLinkEvent ne) {
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
