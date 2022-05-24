package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.core.model.Host;
import es.uvigo.esei.dsbox.core.model.Link;
import es.uvigo.esei.dsbox.core.model.Network;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NetworkEditorPanel extends ElementPanel {

    private TextField id;

    private TextField name;

    private TextField description;

    private TextField label;

    private TextField netAddress;
    private TextField netMask;
    private TextField netBroadcast;

    private Label networkType;

    private Label numberOfInputConnections;

    private TableView<Link> inputLinksTable;

    private Network network;
    private List<Link> inputLinks;

    public NetworkEditorPanel(Network network, List<Link> inputLinks) {
        super();
        this.network = network;
        this.inputLinks = inputLinks;
        initComponents();

    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
        initComponents();
    }

    public void setInputLinks(List<Link> inputLinks) {
        this.inputLinks = inputLinks;
        initComponents();
    }

    @Override
    protected void initComponents() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));

        grid.getColumnConstraints().addAll(new ColumnConstraints(100, 120, 200, Priority.SOMETIMES, HPos.LEFT, true),
                new ColumnConstraints(150, 350, 450, Priority.ALWAYS, HPos.LEFT, true));

        id = new TextField(network.getId());
        id.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 0, "Id :", id);

        name = new TextField(network.getName());
        name.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 1, "Name :", name);

        networkType = new Label(network.getType().name());
        addPropertyControl(grid, 2, "Network type :", networkType);

        description = new TextField(network.getDescription());
        description.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 3, "Description :", description);

        label = new TextField(network.getLabel());
        label.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 4, "Label :", label);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 5, 2, 1);

        netAddress = new TextField(network.getNetAddress());
        netAddress.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 6, "Network address :", netAddress);

        netMask = new TextField(network.getNetMask());
        netMask.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 7, "Network mask :", netMask);

        netBroadcast = new TextField(network.getBroadcast());
        netBroadcast.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 8, "Broadcast address :", netBroadcast);

        if ((inputLinks != null) && (!inputLinks.isEmpty())) {
            grid.add(new Separator(Orientation.HORIZONTAL), 0, 9, 2, 1);

            numberOfInputConnections = new Label(Integer.toString(inputLinks.size()));
            addPropertyControl(grid, 10, "# input connections :", numberOfInputConnections);
            inputLinksTable = createInputLinkTable();

            grid.add(inputLinksTable, 0, 11, 2, 3);
        }

        Parent buttons = createButtons();
        save.setDisable(true);

        VBox panel = new VBox(8, grid, new Separator(Orientation.HORIZONTAL), buttons);

        this.getChildren().clear();
        this.getChildren().add(panel);
    }

    private TableView createInputLinkTable() {
        TableView<Link> table = new TableView(FXCollections.observableArrayList(inputLinks));
        table.setEditable(false);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
//        table.setPrefWidth(360);

        //TableColumn<Link, Integer> positionColumn = new TableColumn<>("Interface");
        //positionColumn.setCellValueFactory(new PropertyValueFactory<>("interfaceOrder"));
        //positionColumn.setResizable(false);
        TableColumn<Link, String> destinationColumn = new TableColumn<>("Source");
        destinationColumn.setCellValueFactory((TableColumn.CellDataFeatures<Link, String> cellData) -> {
            Host host = cellData.getValue().getHost();
            String destinationDescription = host.getName() + " [" + host.getHostType().getDescription() + "]";
            return new SimpleStringProperty(destinationDescription);
        });

        TableColumn<Link, String> addressColumn = new TableColumn<>("IPv4 address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));

        TableColumn<Link, String> netmaskColumn = new TableColumn<>("Mask");
        //netmaskColumn.setCellValueFactory(new PropertyValueFactory<>("netMask"));
        netmaskColumn.setCellValueFactory((TableColumn.CellDataFeatures<Link, String> cellData) -> {
            Network network = cellData.getValue().getNetwork();
            return new SimpleStringProperty(network.getNetMask());
        });

        TableColumn<Link, String> broadcastColumn = new TableColumn<>("BCast address");
        //broadcastColumn.setCellValueFactory(new PropertyValueFactory<>("broadcast"));
        broadcastColumn.setCellValueFactory((TableColumn.CellDataFeatures<Link, String> cellData) -> {
            Network network = cellData.getValue().getNetwork();
            return new SimpleStringProperty(network.getBroadcast());
        });

        table.getColumns().addAll(destinationColumn, addressColumn, netmaskColumn, broadcastColumn);

        return table;
    }

    @Override
    public void updateModelValues() {
        network.setId(id.getText());
        network.setName(name.getText());
        network.setLabel(label.getText());
        network.setDescription(description.getText());
    }

}
