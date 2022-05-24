package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventType;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkNodeEvent;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventProcessor;
import javafx.scene.control.Label;

public abstract class NodeView extends Region {

    public final static int NODEVIEW_IMAGE_SIZE = 128;

    protected Label label;
    protected Position position;

    protected NetworkEventProcessor networkEventProcessor;

    public NodeView(Position position, NetworkEventProcessor networkEventProcessor) {
        this.position = position;
        this.label = new Label();
        this.networkEventProcessor = networkEventProcessor;

        setEventListeners();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void setLabelText(String newLabelText) {
        this.label.setText(newLabelText);
    }

    public void setNetworkEventProcessor(NetworkEventProcessor networkEventListener) {
        this.networkEventProcessor = networkEventListener;
    }

//    public void updatePosition(Position newPosition) {
//        // Move node
//        this.setTranslateX(newPosition.getX());
//        this.setTranslateY(newPosition.getY());
//        this.position = newPosition;
//
//        // Move links
//        if (this.getInputLinks() != null) {
//            for (LinkView inputLink : this.getInputLinks()) {
//                inputLink.setEndX(newPosition.getX() + (this.getWidth() / 2));
//                inputLink.setEndY(newPosition.getY() + (this.getHeight() / 2));
//            }
//        }
//        if (this.getOutputLinks() != null) {
//            for (LinkView outputLink : this.getOutputLinks()) {
//                outputLink.setStartX(newPosition.getX() + (this.getWidth() / 2));
//                outputLink.setStartY(newPosition.getY() + (this.getHeight() / 2));
//            }
//        }
//    }


    private void setEventListeners() {
        this.setOnMouseClicked((MouseEvent me) -> {
            Position p = new Position(this.getTranslateX() + me.getX(), this.getTranslateY() + me.getY());

            if (me.getButton() == MouseButton.SECONDARY) {
                NetworkNodeEvent ne = new NetworkNodeEvent(NetworkEventType.SECONDARY_CLICKED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onNodeSecondaryClicked(ne);
            } else if (me.getClickCount() > 1) {
                NetworkNodeEvent ne = new NetworkNodeEvent(NetworkEventType.DOUBLE_CLICKED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onNodeDoubleClicked(ne);
            } else {
                NetworkNodeEvent ne = new NetworkNodeEvent(NetworkEventType.CLICKED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onNodeClicked(ne);
            }
            me.consume();
        });

        this.setOnMousePressed((MouseEvent me) -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                Position p = new Position(this.getTranslateX() + me.getX(), this.getTranslateY() + me.getY());

                NetworkNodeEvent ne = new NetworkNodeEvent(NetworkEventType.SELECTED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onNodeSelected(ne);
                me.consume();
            }
        });

        this.setOnMouseReleased((MouseEvent me) -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                Position p = new Position(this.getTranslateX() + me.getX(), this.getTranslateY() + me.getY());

                NetworkNodeEvent ne = new NetworkNodeEvent(NetworkEventType.UNSELECTED, this, p);
                ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
                this.networkEventProcessor.onNodeUnselected(ne);
                me.consume();
            }
        });

        this.setOnMouseDragged((MouseEvent me) -> {
            Position p = new Position(this.getTranslateX() + me.getX(), this.getTranslateY() + me.getY());
            NetworkNodeEvent ne = new NetworkNodeEvent(NetworkEventType.DRAGGED, this, p);
            ne.setScreenPosition(new Position(me.getScreenX(), me.getScreenY()));
            this.networkEventProcessor.onNodeDragged(ne);
            me.consume();
        });
    }

    public abstract boolean canStartLink();

    public abstract boolean canEndLink();

    public abstract void createGraphicalRepresentation();
//    {
//                //StackPane pane = new StackPane();
//        //pane.getChildren().addAll(new ImageView(new Image("file:PC.png")), new Label(e.getName()));
//        ImageView image = new ImageView(new Image("file:PC.png"));
//        //Rectangle r = new Rectangle(image.getFitWidth(), image.getFitHeight(), Color.WHITE);
//        //r.setStrokeWidth(3);
//        //r.setStroke(Color.RED);
//        //r.setStrokeType(StrokeType.OUTSIDE);
//        //r.setStrokeLineJoin(StrokeLineJoin.ROUND);
//
//        //VBox pane = new VBox(new StackPane(r, image), new Label(node.getName()));
//        VBox pane = new VBox(image, new Label(node.getName()));
//        pane.setAlignment(Pos.CENTER);
//
//        this.getChildren().add(pane);
//
//     
//    }

}
