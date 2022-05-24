package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.core.model.Network;
import es.uvigo.esei.dsbox.gui.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventProcessor;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class NetworkNodeView extends NodeView {

    private Network network;

    private List<LinkView> inputLinks;

    public NetworkNodeView(Network network, Position position, NetworkEventProcessor networkEventProcessor) {
        super(position, networkEventProcessor);
        this.network = network;
        this.setLabelText(network.getName());

        this.inputLinks = new ArrayList<>();
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean canStartLink() {
        return false;
    }

    @Override
    public boolean canEndLink() {
        return true;
    }

    public List<LinkView> getInputLinks() {
        return inputLinks;
    }

    public void setInputLinks(List<LinkView> inputLinks) {
        this.inputLinks = inputLinks;
    }

    public void addInputLink(LinkView link) {
        this.inputLinks.add(link);
    }

    public void removeInputLink(LinkView link) {
        this.inputLinks.remove(link);
    }

    @Override
    public void createGraphicalRepresentation() {
        Image image = null;
        label = new Label(this.network.getName());
        Position labelPostion = null;
        switch (this.network.getType()) {
            case SWITCH:
                image = new Image(Utils.loadResource("images/NETWORK_SWITCH.png"), NODEVIEW_IMAGE_SIZE, NODEVIEW_IMAGE_SIZE, true, true);
                label.setTextFill(Color.WHITE);
                labelPostion = new Position(0, 50);
                break;
            case HUB:
                image = new Image(Utils.loadResource("images/NETWORK_HUB2.png"), NODEVIEW_IMAGE_SIZE, NODEVIEW_IMAGE_SIZE, true, true);
                label.setTextFill(Color.WHITE);
                labelPostion = new Position(0, 50);
                break;
            case EXTERNAL_BRIDGED:
                image = new Image(Utils.loadResource("images/NETWORK_BRIDGE.png"), NODEVIEW_IMAGE_SIZE, NODEVIEW_IMAGE_SIZE, true, true);
                label.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                labelPostion = new Position(0, -20);
                break;
            case EXTERNAL_NATTED:
                image = new Image(Utils.loadResource("images/NETWORK_NAT.png"), NODEVIEW_IMAGE_SIZE, NODEVIEW_IMAGE_SIZE, true, true);
                label.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                labelPostion = new Position(0, -20);
                break;
        }

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(new ImageView(image), label);
        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 1.2 * Font.getDefault().getSize()));
        label.setTranslateY(labelPostion.getY());

        this.getChildren().add(pane);
    }

}
