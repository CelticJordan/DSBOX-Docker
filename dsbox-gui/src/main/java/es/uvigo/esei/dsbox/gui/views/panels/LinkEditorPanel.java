package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.core.model.Link;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class LinkEditorPanel extends ElementPanel {

    private Link link;

    private Label hostData;
    private Label networkData;
    private TextField interfaceOrder;
    private TextField description;
    private TextField ipAddress;
    private TextField netMask;
    private TextField broadcast;
    private CheckBox enabled;

    public LinkEditorPanel(Link link) {
        super();
        this.link = link;
        initComponents();
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
        initComponents();
    }

 

    @Override
    protected void initComponents() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));
        grid.getColumnConstraints().addAll(new ColumnConstraints(100, 120, 200, Priority.ALWAYS, HPos.LEFT, true),
                new ColumnConstraints(200, 250, 400, Priority.SOMETIMES, HPos.LEFT, true));

//        grid.add(new Separator(Orientation.HORIZONTAL), 0, 0, 2, 1);
        hostData = new Label(link.getHost().getName() + " [" + link.getHost().getHostType().getOs() + ", " + link.getHost().getHostType().getArch() + "]");
        addPropertyControl(grid, 0, "Host :", hostData);

        networkData = new Label(link.getNetwork().getName() + " [" + link.getNetwork().getType() + "]");
        addPropertyControl(grid, 1, "Network device :", networkData);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 2, 2, 1);

        interfaceOrder = new TextField(Integer.toString(link.getInterfaceOrder()));
        interfaceOrder.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 3, "Interface order :", interfaceOrder);

        enabled = new CheckBox();
        enabled.setSelected(link.isEnabled());
        enabled.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            save.setDisable(false);
        });
        addPropertyControl(grid, 4, "Enabled :", enabled);

        description = new TextField(link.getDescription());
        description.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 5, "Description :", description);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 6, 2, 1);

        ipAddress = new TextField(link.getIpAddress());
        ipAddress.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 7, "IPv4 address :", ipAddress);

        netMask = new TextField(link.getNetwork().getNetMask());
        netMask.setEditable(false);
        netMask.setDisable(true);
        //netMask.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 8, "IPv4 netmask :", netMask);

        broadcast = new TextField(link.getNetwork().getBroadcast());
        broadcast.setEditable(false);
        broadcast.setDisable(true);
        //broadcast.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 9, "BCast address :", broadcast);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 10, 2, 1);

        Parent buttons = createButtons("Update Link", "Cancel", true);
        save.setDisable(true);

        HBox panel = new HBox(8, grid, buttons);
        this.getChildren().clear();
        this.getChildren().add(panel);
    }

    @Override
    public void updateModelValues() {        
        link.setInterfaceOrder(Integer.parseInt(interfaceOrder.getText()));
        link.setEnabled(enabled.isSelected());
        link.setDescription(description.getText());
        link.setIpAddress(ipAddress.getText());
        //link.setNetMask(netMask.getText());
        //link.setBroadcast(broadcast.getText());
    }
}
