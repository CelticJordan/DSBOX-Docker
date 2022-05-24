package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventType;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkLinkEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventProcessor;

public class LinkView extends Line {

    public static final int LINE_WIDTH = 6;
    public static final Color LINE_COLOR = Color.gray(0.3);

    private HostNodeView source;
    private NetworkNodeView destination;

    private Link link;

    private NetworkEventProcessor networkEventProcessor;

    public LinkView(HostNodeView source, NetworkNodeView destination, Link link, NetworkEventProcessor networkEventProcessor) {
        this.source = source;
        this.destination = destination;

        this.link = link;

        this.networkEventProcessor = networkEventProcessor;

        this.setOnMousePressed((MouseEvent me) -> {
            Position p = new Position(me.getSceneX(), me.getSceneY());
            NetworkLinkEvent ne = new NetworkLinkEvent(NetworkEventType.SELECTED, this, p);
            ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
            this.networkEventProcessor.onLinkSelected(ne);
        });

        this.setOnMouseClicked((MouseEvent me) -> {
            Position p = new Position(me.getSceneX(), me.getSceneY());

            if (me.getButton() == MouseButton.SECONDARY) {
                NetworkLinkEvent ne = new NetworkLinkEvent(NetworkEventType.SECONDARY_CLICKED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onLinkSecondaryClicked(ne);
            } else if (me.getClickCount() > 1) {
                NetworkLinkEvent ne = new NetworkLinkEvent(NetworkEventType.DOUBLE_CLICKED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onLinkDoubleClicked(ne);
            } else {
                NetworkLinkEvent ne = new NetworkLinkEvent(NetworkEventType.CLICKED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onLinkClicked(ne);
            }
            me.consume();
        });

        this.setSmooth(true);
        this.setStrokeWidth(LINE_WIDTH);
        this.setStroke(LINE_COLOR);
    }

    public void setNetworkEventListener(NetworkEventProcessor networkEventListener) {
        this.networkEventProcessor = networkEventListener;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public HostNodeView getSource() {
        return source;
    }

    public void setSource(HostNodeView source) {
        this.source = source;
        updateStart(source.getPosition());
    }

    public NetworkNodeView getDestination() {
        return destination;
    }

    public void setDestination(NetworkNodeView destination) {
        this.destination = destination;
    }

    public void setTemporaryEnd(Position endPosition) {
        updateEnd(endPosition);
    }

    private void updateEnd(Position position) {
        this.setEndX(position.getX());
        this.setEndY(position.getY());
    }

    private void updateStart(Position position) {
        this.setStartX(position.getX());
        this.setStartY(position.getY());
    }
}
