package es.uvigo.esei.dsbox.gui.editor;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.gui.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import es.uvigo.esei.dsbox.gui.editor.events.NetworkEventProcessor;
import java.util.ArrayList;
import java.util.List;

public class HostNodeView extends NodeView {

    private static final String DEFAULT_HOST_IMAGE = "images/HOST.png";

    private Host host;
    private List<LinkView> outputLinks;

    public HostNodeView(Host host, Position position, NetworkEventProcessor networkEventProcessor) {
        super(position, networkEventProcessor);
        this.host = host;
        this.setLabelText(host.getName());

        this.outputLinks = new ArrayList<>();
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    @Override
    public boolean canStartLink() {
        return true;
    }

    @Override
    public boolean canEndLink() {
        return false;
    }

    public List<LinkView> getOutputLinks() {
        return outputLinks;
    }

    public void setOutputLinks(List<LinkView> outputLinks) {
        this.outputLinks = outputLinks;
    }

    public void addOutputLink(LinkView link) {
        this.outputLinks.add(link);
    }

    public void removeOutputLink(LinkView link) {
        this.outputLinks.remove(link);
    }

    @Override
    public void createGraphicalRepresentation() {

        Node image = Utils.createHostTypeImage(host.getHostType(), DEFAULT_HOST_IMAGE, NODEVIEW_IMAGE_SIZE, true);
        label = new Label(this.host.getName());
        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 1.2 * Font.getDefault().getSize()));

        VBox pane = new VBox();
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(image, label);
        this.getChildren().add(pane);
    }

}
