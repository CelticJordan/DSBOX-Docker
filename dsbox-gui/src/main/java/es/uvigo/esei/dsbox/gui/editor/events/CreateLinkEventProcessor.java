package es.uvigo.esei.dsbox.gui.editor.events;


import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.gui.editor.HostNodeView;
import es.uvigo.esei.dsbox.gui.editor.LinkView;
import es.uvigo.esei.dsbox.gui.editor.NetworkNodeView;
import es.uvigo.esei.dsbox.gui.editor.NetworkView;
import es.uvigo.esei.dsbox.gui.editor.NodeView;
import es.uvigo.esei.dsbox.gui.editor.Position;
import es.uvigo.esei.dsbox.gui.editor.actions.CreateLinkAction;
import es.uvigo.esei.dsbox.gui.editor.actions.EditActionDestination;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author ribadas
 */
public class CreateLinkEventProcessor extends BaseNetworkEventProcessor {
    
    private final static Color TEMPORAL_LINE_COLOR = Color.rgb(255, 0, 0, 0.6);
    private final static int TEMPORAL_LINE_WIDTH = LinkView.LINE_WIDTH;

    private HostNodeView startNode;
    private NetworkNodeView endNode;

    private Position initPosition;
    //private Position dragAnchor;

    private Line temporalLine;
    //private int temporalLineIndex;

    public CreateLinkEventProcessor(NetworkView networkView, EditActionDestination actionDestination) {
        super(networkView, actionDestination);
    }


    @Override
    public void onNodeClicked(NetworkNodeEvent ne) {
        if (!linkBeingCreated() && validStartNode(ne.getNodeView())) {
            HostNodeView startHost = (HostNodeView) ne.getNodeView();
            startLink(startHost, ne.getPosition());
        } else if (validEndNode(ne.getNodeView())) {
            NetworkNodeView endNetwork = (NetworkNodeView) ne.getNodeView();
            finishLink(endNetwork, ne.getPosition());
        }
    }
//    @Override
//    public void onNodeDragged(NetworkNodeEvent ne) {
//        NodeView nv = (NodeView) ne.nodeView;
//        System.out.println("[LINK] [DRAGGED]  " + nv.getNode().getName() + "  (" + ne.position.x + ", " + ne.position.y + ")");
//    }

    @Override
    public void onNodeSecondaryClicked(NetworkNodeEvent ne) {
        if (linkBeingCreated()) {
            cancelCurrentLink();
        }
    }

    @Override
    public void onCanvasClicked(NetworkEvent ne) {
        if (linkBeingCreated()) {
            cancelCurrentLink();
        }
    }

    private void cancelCurrentLink() {
        // Abort link creation
        startNode = null;
        endNode = null;

        // Eliminar linea temporal
        networkView.clearTemporalLine(temporalLine);
        temporalLine = null;
    }

    @Override
    public void onMouseMoved(NetworkEvent ne) {
        if (startNode != null) {
            updateTemporalLine(ne);
        }
    }

    private boolean linkBeingCreated() {
        return (startNode != null);
    }

    private boolean validStartNode(NodeView node) {
        return node.canStartLink();
    }

    private void startLink(HostNodeView startHost, Position position) {
        startNode = startHost;

        initPosition = new Position(startNode.getTranslateX() + (startNode.getWidth() / 2), startNode.getTranslateY() + (startNode.getHeight() / 2));

        double newXPosition = position.getX();
        double newYPosition = position.getY();  

        temporalLine = new Line(initPosition.getX(), initPosition.getY(), newXPosition, newYPosition);

        temporalLine.setSmooth(true);
        temporalLine.setStrokeWidth(TEMPORAL_LINE_WIDTH);
        temporalLine.setStroke(TEMPORAL_LINE_COLOR);
        temporalLine.setStrokeLineCap(StrokeLineCap.ROUND);
        temporalLine.getStrokeDashArray().addAll(15.0, 10.0);
        networkView.addTemporalLine(temporalLine);
        temporalLine.toFront();
    }

    private boolean validEndNode(NodeView node) {
        return (node != startNode) && node.canEndLink();
    }

    private void finishLink(NetworkNodeView endNetwork, Position position) {
        endNode = endNetwork;

        // Eliminar linea temporal
        networkView.clearTemporalLine(temporalLine);
        
        // Crear link        
        int linkPosition = startNode.getHost().getNextLinkPosition();
        Link newLink = new Link(startNode.getHost(), endNode.getNetwork(), linkPosition);
        LinkView newLinkView = new LinkView(startNode, endNode, newLink, networkView);        
        CreateLinkAction action = new CreateLinkAction(networkView, newLinkView);
        actionDestination.processEditAction(action);
        
        startNode = null;
        endNode = null;
    }

    private final static int TEMPORAL_LINE_POINTER_OFFSET = 4;
    
    private void updateTemporalLine(NetworkEvent ne) {
        double newXPosition = ne.getPosition().getX();
        double newYPosition = ne.getPosition().getY();

        // Trick to ensrure MouseClicked activation at destination NodeView
        int correctX = TEMPORAL_LINE_POINTER_OFFSET;
        int correctY = TEMPORAL_LINE_POINTER_OFFSET;

        if (newXPosition >= temporalLine.getStartX()) {
            correctX = -TEMPORAL_LINE_POINTER_OFFSET;
        }
        if (newYPosition >= temporalLine.getStartY()) {
            correctY = -TEMPORAL_LINE_POINTER_OFFSET;
        }

        temporalLine.setEndX(newXPosition + correctX);
        temporalLine.setEndY(newYPosition + correctY);
    }

}
